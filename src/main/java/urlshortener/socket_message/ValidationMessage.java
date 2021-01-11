package urlshortener.socket_message;

public class ValidationMessage {
    private String shortUrl;
    private String url;
    private Boolean valid;
    private Boolean isCSV;
    private Boolean validationMessage;

    public ValidationMessage(String shortUrl, Boolean valid, String url, Boolean isCSV) {
        this.shortUrl = shortUrl;
        this.valid = valid;
        this.isCSV = isCSV;
        this.url = url;
        this.validationMessage = true;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public Boolean getValid() {
        return valid;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getCSV() {
        return isCSV;
    }

    public Boolean getValidationMessage() {
        return validationMessage;
    }
}
