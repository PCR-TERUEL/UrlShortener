package urlshortener.web;

import java.net.URI;

import java.net.URISyntaxException;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import urlshortener.config.JWTTokenUtil;
import urlshortener.domain.JWT;
import urlshortener.domain.ShortURL;
import urlshortener.domain.User;
import urlshortener.service.*;

@Controller
public class UrlShortenerController implements WebMvcConfigurer, ErrorController {
  public static final String HOST = "localhost:8080";
  private final ShortURLService shortUrlService;
  private final ClickService clickService;
  private final SecureUserService secureUserService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JWTTokenUtil jwtTokenUtil;

  public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService, SecureUserService secureUserService) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
    this.secureUserService = secureUserService;
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


  @RequestMapping(value = "/r/{id:(?).*}", method = RequestMethod.GET)
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

  @RequestMapping(value = "/login")
  public String login() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth.getPrincipal() instanceof  UserDetails) {
      return "redirect:/panel";
    }

    return "userlogin";
  }

  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  public ResponseEntity<?> createAuthenticationToken(@RequestParam String username,
                                                     @RequestParam String password,
                                                     HttpServletResponse response) throws Exception {

    System.out.println("Hey, " + username + " " + password);
    authenticate(username, password);
    UserDetails userDetails = secureUserService.loadUserByUsername(username);
    String token = jwtTokenUtil.generateToken(userDetails);
    response.addCookie(new Cookie("token", "Bearer " + token));
    response.addCookie(new Cookie("username", username));

    return ResponseEntity.ok(new JWT(token));
  }

  /**
   * @api {post} /register User register
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

  @RequestMapping(value = "/singup", method = RequestMethod.POST)
  public ResponseEntity<?> register(@RequestParam("username") String username,
                                    @RequestParam("password") String password) {

    if(username.equals("") || password.equals("")){
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    boolean registered = secureUserService.save(username, password);

    if (registered) {
      return new ResponseEntity<>(HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.IM_USED);
    }
  }

  @RequestMapping(value = "/delete", method = RequestMethod.GET)
  public ResponseEntity<?> delete() {
   return new ResponseEntity<>(HttpStatus.CREATED);
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
                                            HttpServletRequest request) {

    String username = jwtTokenUtil.getUsernameFromToken(jwtTokenUtil.getRequestToken(request));
    User u = secureUserService.getUser(username);
    URLValidatorService urlValidator = new URLValidatorService(url);

    if (urlValidator.isValid()) {
      ShortURL su = shortUrlService.save(url, sponsor, String.valueOf(u.getId()), request.getRemoteAddr());
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

  @Async
  @RequestMapping(value = "/userlinks", method = RequestMethod.POST)
  public ResponseEntity<?> getUserLinks(HttpServletRequest request) throws URISyntaxException {
    String username = jwtTokenUtil.getUsernameFromToken(jwtTokenUtil.getRequestToken(request));
    User u = secureUserService.getUser(username);

    List<ShortURL> urlShort = shortUrlService.findByUser(String.valueOf(u.getId()));
    System.out.println("Hi, I'm " + u.getUsername() + "with id: " + u.getId() + " And those are my urls: " + urlShort);

    return new ResponseEntity<>(urlShort, HttpStatus.OK);
  }



  @RequestMapping("/error")
  public String error() {
    return "error_no";
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

  @Override
  public String getErrorPath() {
    return null;
  }

  private void authenticate(String username, String password) throws Exception {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }



}
