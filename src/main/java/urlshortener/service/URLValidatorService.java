package urlshortener.service;

import org.apache.commons.validator.routines.UrlValidator;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLValidatorService {

    private final String url;

    public URLValidatorService(String url) {
        this.url = url;
    }

    /**
     * Check if a URL is reachable and returns 200
     *
     * @return valid?
     */
    public boolean isValid() {
        UrlValidator urlValidator = new UrlValidator(new String[] {"http",
                "https"});
        if (urlValidator.isValid(url)) {
            try {
                HttpURLConnection http = (HttpURLConnection)new URL(url).openConnection();
                if (http.getResponseCode() == 200) {
                    return true;
                }
            } catch (Exception e) { /* return false */ }
        }

        return false;
    }

}
