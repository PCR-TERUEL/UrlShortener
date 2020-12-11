package urlshortener.socket_message;

public class ShorUrlPetitionMessage {
    private String url;
    private String sponsor;
    private String idToken;

    public ShorUrlPetitionMessage(String url, String idToken) {
        this.url = url;
        this.sponsor = "";
        this.idToken = idToken;
    }

    public ShorUrlPetitionMessage(String url, String sponsor, String idToken){
        this.url = url;
        this.sponsor = sponsor;
        this.idToken = idToken;
    }

    public ShorUrlPetitionMessage() {
    }

    public String getUrl() {
        return url;
    }

    public String getSponsor() {
        return sponsor;
    }

    public String getIdToken() {
        return idToken;
    }
}
