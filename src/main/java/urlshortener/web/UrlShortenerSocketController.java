package urlshortener.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import urlshortener.config.JWTTokenUtil;
import urlshortener.domain.ShortURL;
import urlshortener.domain.User;
import urlshortener.service.SecureUserService;
import urlshortener.service.ShortURLService;
import urlshortener.service.TaskQueueService;
import urlshortener.service.URLValidatorService;
import urlshortener.socket_message.ShorUrlPetitionMessage;
import urlshortener.socket_message.ShortUrlResponseMessage;

import java.security.Principal;

@Controller
public class UrlShortenerSocketController {
    private ShortURLService shortUrlService;
    private SimpMessageSendingOperations simpMessageSendingOperations;
    private final SecureUserService secureUserService;
    private final TaskQueueService taskQueueService;

    @Autowired
    private JWTTokenUtil jwtTokenUtil;

    public UrlShortenerSocketController(ShortURLService shortUrlService, SecureUserService secureUserService,
                                        SimpMessageSendingOperations simpMessageSendingOperations,
                                        TaskQueueService taskQueueService) {
        this.shortUrlService = shortUrlService;
        this.secureUserService = secureUserService;
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        this.taskQueueService = taskQueueService;
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
    @SendToUser("/url_shortener/short_url")
    public ShortUrlResponseMessage shortener(ShorUrlPetitionMessage petition,
                                             @Header("simpSessionId") String sessionId) throws InterruptedException {
        int numMonth;
        String username = jwtTokenUtil.getUsernameFromToken(petition.getIdToken().substring(7,
                petition.getIdToken().length()-1));
        User u = secureUserService.getUser(username);

        try {
            numMonth = Integer.parseInt(petition.getNumMonth());
        } catch (NullPointerException | NumberFormatException exception){
            numMonth = -1;
        }
        ShortURL su = shortUrlService.save(petition.getUrl(), petition.getSponsor(),
                String.valueOf(u.getId()), "", numMonth);
        ShortUrlResponseMessage outMessage = new ShortUrlResponseMessage(su, false, petition.isDocumentCsv(),
                petition.getIdToken());

        //sesion id info del websockets para mandar a un usuario concreto.
        //url sin acortar
        //url acortada
        System.out.println(su.getUri().toString());
        taskQueueService.publishValidationJob(sessionId, petition.getUrl(), su.getUri().toString());

        return outMessage;
    }
    /* Para el metodo de enviar mensajes sin usar el return
    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setHeader(SimpMessageHeaderAccessor.SESSION_ID_HEADER, sessionId);
        simpMessageSendingOperations.convertAndSendToUser(sessionId, "/url_shortener/short_url", outMessage,
            accessor.getMessageHeaders());*/
}
