package urlshortener.socket_message;

import java.net.URI;
import urlshortener.domain.ShortURL;

public class ShortUrlResponseMessage {
    private URI uri;
    private String target;
    private Long clicks;
    private boolean validationMessage;

    private boolean error;

    public ShortUrlResponseMessage() {
    }

    public ShortUrlResponseMessage(ShortURL shortURL, boolean error, String idToken) {
        this.uri = shortURL !=null ? shortURL.getUri() : null;
        this.target = shortURL.getTarget();
        this.clicks = shortURL.getClicks();
        this.error = error;
        this.validationMessage = false;
    }

    public boolean isValidationMessage() {
        return validationMessage;
    }

    public URI getUri() {
        return uri;
    }

    public String getTarget() {
        return target;
    }

    public Long getClicks() {
        return clicks;
    }

    public boolean isError() {
        return error;
    }
}
