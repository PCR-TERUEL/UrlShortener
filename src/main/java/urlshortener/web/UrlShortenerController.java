package urlshortener.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import urlshortener.config.JWTTokenUtil;
import urlshortener.domain.JWT;
import urlshortener.domain.ShortURL;
import urlshortener.domain.User;
import urlshortener.repository.impl.MetricsRepository;
import urlshortener.service.*;
import urlshortener.service.Tasks.TaskQueueService;

@RestController
public class UrlShortenerController implements WebMvcConfigurer, ErrorController {
  public static final String HOST = "localhost:8080";
  private final ShortURLService shortUrlService;
  private final ClickService clickService;
  private final TaskQueueService taskQueueService;

  private final SecureUserService secureUserService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private MetricsRepository metricsRepository;

  @Autowired
  private JWTTokenUtil jwtTokenUtil;

  public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService, SecureUserService secureUserService, TaskQueueService taskQueueService) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
    this.secureUserService = secureUserService;
    this.taskQueueService = taskQueueService;

  }

  @GetMapping(value = "/test")
  public ResponseEntity<?> test() {
    //taskQueueService.send("validation_job", "uno");
    //taskQueueService.publishValidationJob("123", "https://www.eroski.com", "5678", true);
    return null;
  }

  /**
   * Map static content to root
   *
   * @param registry
   */

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static");
  }

  @Operation(summary = "Redirect to correspondent URL by the given hash")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "404", description = "URL not found", content = @Content),
          @ApiResponse(responseCode = "400", description = "URL is not validated yet", content = @Content),
          @ApiResponse(responseCode = "307", description = "Redirect OK")
  })

  @GetMapping(value = "/r/{id:(?).*}")
  public ResponseEntity<?> redirectTo(@PathVariable String id,
                                      HttpServletRequest request) {
    ShortURL l = shortUrlService.findByKey(id);
    if (l != null) {
      if(shortUrlService.isExpired(id)) {
        shortUrlService.delete(l.getHash());
        return new ResponseEntity<>("Limite temporal invalido", HttpStatus.NOT_FOUND);
      } else if (!shortUrlService.isValidated(id)) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
      clickService.saveClick(id, extractIP(request));
      return createSuccessfulRedirectToResponse(l);
    } else {
      return new ResponseEntity<>("No existe la URL acortada", HttpStatus.NOT_FOUND);
    }

  }

  @PostMapping(value = "/authenticate")
  @Operation(summary = "Authenticate a user to get JWT")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "404", description = "Wrong username or password", content = @Content),
          @ApiResponse(responseCode = "201", description = "Authentication OK", content = {
                  @Content(mediaType = "application/json", schema = @Schema(implementation = JWT.class))
          }),
  })
  public ResponseEntity<?> createAuthenticationToken(@RequestParam String username,
                                                     @RequestParam String password,
                                                     HttpServletResponse response)  {

    try {
      Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      SecurityContextHolder.getContext().setAuthentication(auth);
      UserDetails userDetails = (UserDetails) auth.getPrincipal();
      String token = jwtTokenUtil.generateToken(userDetails);
      response.addCookie(new Cookie("token", "Bearer " + token));
      response.addCookie(new Cookie("username", username));
      return ResponseEntity.ok(new JWT(token));
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }


  @PostMapping(value = "/singup")
  @Operation(summary = "Sing up a user")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "400", description = "Wrong parameters"),
          @ApiResponse(responseCode = "409", description = "Username already exists"),
          @ApiResponse(responseCode = "201", description = "User registered successfully"),
  })
  public ResponseEntity<?> register(@RequestParam("username") String username,
                                    @RequestParam("password") String password) {
    if(username.equals("") || password.equals("")){
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    boolean registered = secureUserService.save(username, password);

    if (registered) {
      return new ResponseEntity<>(HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
  }


  @Async
  @GetMapping(value = "/userlinks")
  @Operation(summary = "Get all links of an user")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User links retrieved successfully", content = {
                  @Content(mediaType = "application/json", schema = @Schema(implementation = ShortURL.class))
          }),
  })
  public ResponseEntity<?> getUserLinks(HttpServletRequest request) throws URISyntaxException {
    System.out.println("ENTROOOOOOOOOOOOOOOOOOOOOO");
    UserDetails ud = (UserDetails) (SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    User u = secureUserService.getUser(ud.getUsername());
    if(metricsRepository.contains(u.getId())){
      System.out.println("Getting from metrics");
      taskQueueService.publishMetricJob(u.getId());
      return new ResponseEntity<>(shortUrlService.metricToJSON(metricsRepository.getMetrics(u.getId())), HttpStatus.OK);
    }else{
      System.out.println("Getting from db");

      List<ShortURL> urlShort = shortUrlService.findByUser(String.valueOf(u.getId()));
      taskQueueService.publishMetricJob(u.getId());
      return new ResponseEntity<>(shortUrlService.toJson(urlShort), HttpStatus.OK);
    }
  }


  @PostMapping(value = "/users-information")
  @Operation(summary = "Get all users information")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "All users retrieved successfully", content = {
                  @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
          })
  })
  public ResponseEntity<?> getUsers() {
    return new ResponseEntity<>(secureUserService.getUsers(), HttpStatus.OK);
  }


  @Async
  @DeleteMapping(value = "/user/{id}")
  @Operation(summary = "Delete user")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "User deleted OK"),
          @ApiResponse(responseCode = "404", description = "User does not exists"),
  })
  public ResponseEntity<?> deleteUser(@PathVariable int id) {
    if (secureUserService.deleteUser(id)){
      return new ResponseEntity<>(HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  private String extractIP(HttpServletRequest request) {
    return request.getRemoteAddr();
  }

  private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
    HttpHeaders h = new HttpHeaders();
    h.setLocation(URI.create(l.getTarget()));
    return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
  }

  @Override
  public String getErrorPath() {
    return null;
  }

}
