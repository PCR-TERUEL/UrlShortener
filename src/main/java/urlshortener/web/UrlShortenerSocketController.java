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
import urlshortener.service.Tasks.TaskQueueService;
import urlshortener.socket_message.ShortUrlPetitionMessage;
import urlshortener.socket_message.ShortUrlResponseMessage;
import urlshortener.socket_message.ValidationMessage;

import java.net.URI;

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
     * This method receive the requests of short a new URL, create a shortUrl, send the tasks to validate the URL and
     * response with the shortUrl without validation.
     * @param petition: message send from the client with the new URL to short
     * @param sessionId: id of the web socket connection of the client who sent the message.
     * @return the shortUrl without validation of the new URL. But if are a problem send a message without the shortURL.
     */
    @Async
    @MessageMapping("/link")
    @SendToUser("/url_shortener/short_url")
    public ShortUrlResponseMessage shortener(ShortUrlPetitionMessage petition,
                                             @Header("simpSessionId") String sessionId) {
        try {
            int numMonth;
            String username = jwtTokenUtil.getUsernameFromToken(petition.getIdToken().substring(7,
                    petition.getIdToken().length() - 1));
            User u = secureUserService.getUser(username);

            try {
                if(petition.getNumMonth().equals("")){
                    numMonth = -1;
                } else {
                    numMonth = Integer.parseInt(petition.getNumMonth());
                }
            } catch (NullPointerException exception){ // | NumberFormatException exception){
                numMonth = -1;
            } catch(NumberFormatException exception){
                return new ShortUrlResponseMessage(new ShortURL(), true,
                        "null");
            }
            ShortURL su = shortUrlService.save(petition.getUrl(), petition.getSponsor(),
                    String.valueOf(u.getId()), "", numMonth);
            su.setUri(new URI("http://" + UrlShortenerController.HOST + "/r/" + su.getHash()));
            ShortUrlResponseMessage outMessage = new ShortUrlResponseMessage(su, false, petition.getIdToken());

            taskQueueService.publishValidationJob(sessionId, petition.getUrl(), su.getUri().toString(),
                    petition.isDocumentCsv());

            return outMessage;
        }catch (Exception e){
            return new ShortUrlResponseMessage(new ShortURL(), true,
                    "null");
        }
    }

    /**
     * This method send the message with the result of the validation of a URL.
     * @param shortUrl: shortUrl created as of the url validated
     * @param valid: result of the validation
     * @param sessionId: sessionId of the client to send the message
     */
    public void sendValidation(String shortUrl, Boolean valid, String sessionId, String url, Boolean isCSV){
        ValidationMessage validationMessage = new ValidationMessage(shortUrl, valid, url, isCSV);
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setHeader(SimpMessageHeaderAccessor.SESSION_ID_HEADER, sessionId);
        simpMessageSendingOperations.convertAndSendToUser(sessionId, "/url_shortener/short_url",
                validationMessage,
                accessor.getMessageHeaders());
        shortUrlService.validate(url, valid);
    }
}
