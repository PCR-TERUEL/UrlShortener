package urlshortener.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urlshortener.config.JWTTokenUtil;
import urlshortener.domain.ShortURL;
import urlshortener.domain.User;
import urlshortener.service.SecureUserService;
import urlshortener.service.ShortURLService;
import urlshortener.service.URLValidatorService;
import urlshortener.socket_message.ShorUrlPetitionMessage;
import urlshortener.socket_message.ShortUrlResponseMessage;

@Controller
public class UrlShortenerSocketController {
    private ShortURLService shortUrlService;
    private final SecureUserService secureUserService;
    @Autowired
    private JWTTokenUtil jwtTokenUtil;

    public UrlShortenerSocketController(ShortURLService shortUrlService, SecureUserService secureUserService) {
        this.shortUrlService = shortUrlService;
        this.secureUserService = secureUserService;

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

    @Async
    @MessageMapping("/link")
    @SendTo("/url_shortener/short_url")
    public ShortUrlResponseMessage shortener(ShorUrlPetitionMessage petition) {
        String username = jwtTokenUtil.getUsernameFromToken(petition.getIdToken().substring(7, petition.getIdToken().length()-1));
        User u = secureUserService.getUser(username);
        URLValidatorService urlValidator = new URLValidatorService(petition.getUrl());

        if (urlValidator.isValid()) {
            ShortURL su = shortUrlService.save(petition.getUrl(), petition.getSponsor(), String.valueOf(u.getId()), "");
            return new ShortUrlResponseMessage(su, false);
        }
        return new ShortUrlResponseMessage(null, false);
    }
}
