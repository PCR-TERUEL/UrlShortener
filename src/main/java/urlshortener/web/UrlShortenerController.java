package urlshortener.web;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import urlshortener.domain.ShortURL;
import urlshortener.domain.User;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.service.URLValidatorService;
import urlshortener.service.UserService;

@RestController
public class UrlShortenerController implements WebMvcConfigurer {
  public static final String HOST = "localhost:8080";
  private static final String STATUS_OK = "OK";
  private static final String STATUS_ERROR = "ERROR";
  //public static final String HOST = "localhost";
  private final ShortURLService shortUrlService;
  private final ClickService clickService;
  private final UserService userService;

  public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService, UserService userService) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
    this.userService = userService;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static");
  }

  /**
   * @api {get} /{id:(?!link|index).*} Shortened url
   * @apiName RedirectTo
   * @apiGroup ShortURL
   *
   * @apiParam {Hash} id Shortened url unique ID.
   *
   * @apiSuccess OK Url Redirect.
   * @apiError UrlNotFound The url was not found.
   */

  @RequestMapping(value = "/{id:(?!link|api|index|login|panel).*}", method = RequestMethod.GET)
  public ResponseEntity<?> redirectTo(@PathVariable String id,
                                      HttpServletRequest request) {
    if(shortUrlService.isExpired(id)) {
      ShortURL l = shortUrlService.findByKey(id);
      shortUrlService.delete(l.getHash());
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      ShortURL l = shortUrlService.findByKey(id);
      if (l != null) {
        clickService.saveClick(id, extractIP(request));
        return createSuccessfulRedirectToResponse(l);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    }
  }

  /**
   * @api {post} /signup User signup
   * @apiName User register
   * @apiGroup User
   *
   * @apiParam {String} username Username.
   * @apiParam {String} password Password.
   *
   * @apiSuccess OK User registered successfully.
   * @apiError  400 Bad user parameters.
   * @apiError 226 Username already exists.
   */

  @RequestMapping(value = "/signup", method = RequestMethod.POST)
  public ResponseEntity<?> register(@RequestParam("username") String username,
                                    @RequestParam("password") String password) {
    if(username.equals("") || password.equals("")){
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    System.out.println("Username: " + username + " Password: " + password);

    User u = userService.save(username, password);

    System.out.println("Test: " + u.getPassword());
    if (u != null) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("uuid", u.getId());
      jsonObject = createJSONResponse(STATUS_OK, jsonObject);
      return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.IM_USED);
    }
  }

  /**
   * @api {post} /login User login
   * @apiName User login
   * @apiGroup User
   *
   * @apiParam {String} username Username.
   * @apiParam {String} password Password.
   *
   * @apiSuccess 202 User login successful.
   * @apiError  400 Bad user parameters.
   * @apiError 203 Wrong user or password.
   */

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<?> login(@RequestParam("username") String username,
                                    @RequestParam("password") String password) {
    if(username.equals("") || password.equals("")){
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    User u = userService.login(username, password);
    if (u != null) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("uuid", u.getId());
      return new ResponseEntity<>(jsonObject, HttpStatus.ACCEPTED);
    } else {
      return new ResponseEntity<>(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }
  }

  /**
   * @api {get} /login User login
   * @apiName User login
   * @apiGroup User
   *
   * @apiParam {String} username Username.
   * @apiParam {String} password Password.
   *
   * @apiSuccess 202 User login successful.
   * @apiError  400 Bad user parameters.
   * @apiError 203 Wrong user or password.
   */

  @RequestMapping(value = "/user-information", method = RequestMethod.GET)
  public ResponseEntity<?> getUsers() {

    List<User> users = userService.getUsers();

    return new ResponseEntity<>(users, HttpStatus.ACCEPTED);

  }



  /**
   * @api {post} /link Create short link
   * @apiName Create short link
   * @apiGroup ShortURL
   *
   * @apiParam {String} url URL you want to short.
   * @apiParam {String} sponsor="sponsor" Sponsor.
   * @apiParam {String} uuid User Id.
   *
   * @apiSuccess 201 Link generated successfully.
   * @apiError  401 User does not exists.
   * @apiError 400 Invalid or unreachable URL.
   */

  @RequestMapping(value = "/link", method = RequestMethod.POST)
  public ResponseEntity<?> shortener(@RequestParam("url") String url,
                                            @RequestParam(value = "sponsor", required = false) String sponsor,
                                            @RequestParam("uuid") String userId,
                                            HttpServletRequest request) {

    URLValidatorService urlValidator = new URLValidatorService(url);
    if(!userService.exists(userId)){
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    if (urlValidator.isValid()) {
      ShortURL su = shortUrlService.save(url, sponsor, userId, request.getRemoteAddr());
      HttpHeaders h = new HttpHeaders();
      h.setLocation(su.getUri());
      return new ResponseEntity<>(su, h, HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * @api {post} /link Get user links
   * @apiName Get user links
   * @apiGroup ShortURL
   *
   * @apiParam {String} url URL you want to short.
   * @apiParam {String} sponsor="sponsor" Sponsor.
   * @apiParam {String} uuid User Id.
   *
   * @apiSuccess 201 Link generated successfully.
   * @apiError  401 User does not exists.
   * @apiError 400 Invalid or unreachable URL.
   */

  @RequestMapping(value = "/userlinks", method = RequestMethod.POST)
  public ResponseEntity<?> getUserLinks(@RequestParam("uuid") String userId,
                                                 HttpServletRequest request) {

    if(!userService.exists(userId)){
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    JSONObject urlShort = shortUrlService.findByUser(userId);
    return new ResponseEntity<>(urlShort, HttpStatus.OK);

  }


  private String extractIP(HttpServletRequest request) {
    return request.getRemoteAddr();
  }

  private JSONObject createJSONResponse(String status, JSONObject jo) {
    JSONObject jsonResponse = new JSONObject();
    jsonResponse.put("status", status);
    if (jo != null) {
      jsonResponse.merge(jo);
    }

    return jsonResponse;
  }

  private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
    HttpHeaders h = new HttpHeaders();
    h.setLocation(URI.create(l.getTarget()));
    return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
  }
}
