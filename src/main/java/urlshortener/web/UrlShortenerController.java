package urlshortener.web;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.RedirectView;
import urlshortener.domain.ShortURL;
import urlshortener.domain.User;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.service.URLValidatorService;
import urlshortener.service.UserService;

@Controller
public class UrlShortenerController implements WebMvcConfigurer {
  public static final String HOST = "localhost:8080";
  private static final String STATUS_OK = "OK";
  private static final String STATUS_ERROR = "ERROR";
  private final ShortURLService shortUrlService;
  private final ClickService clickService;

  private final UserService userService;

  public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService, UserService userService) {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
    this.userService = userService;
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

    User u = userService.save(username, password);

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

    User u = getCurrentUser();

    URLValidatorService urlValidator = new URLValidatorService(url);
    if(!userService.exists(String.valueOf(u.getId()))) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
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

  @RequestMapping(value = "/userlinks", method = RequestMethod.POST)
  public ResponseEntity<?> getUserLinks(HttpServletRequest request) {
    User u = getCurrentUser();

    if(!userService.exists(String.valueOf(u.getId()))) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    JSONObject urlShort = shortUrlService.findByUser(String.valueOf(u.getId()));
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

  private User getCurrentUser() {
    UserDetails ud =  (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userService.getUser(ud.getUsername());
  }

  private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
    HttpHeaders h = new HttpHeaders();
    h.setLocation(URI.create(l.getTarget()));
    return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
  }

}
