package urlshortener.socket_message;

public class ShorUrlPetitionMessage {
    private String url;
    private String sponsor;
    private String idToken;
    private String numMonth;
    private boolean documentCsv;

    public ShorUrlPetitionMessage(String url, String idToken, Boolean documentCsv) {
        this.url = url;
        this.sponsor = "";
        this.idToken = idToken;
        this.documentCsv = documentCsv;
        this.numMonth = "-1";
    }

    public ShorUrlPetitionMessage(String url, String idToken, Boolean documentCsv, String numMonth) {
        this.url = url;
        this.sponsor = "";
        this.idToken = idToken;
        this.documentCsv = documentCsv;
        this.numMonth = numMonth;
    }

    public ShorUrlPetitionMessage(String url, String sponsor, String idToken, boolean documentCsv){
        this.url = url;
        this.sponsor = sponsor;
        this.idToken = idToken;
        this.documentCsv = documentCsv;
        this.numMonth = "-1";
    }

    public ShorUrlPetitionMessage(String url, String sponsor, String idToken, boolean documentCsv, String numMonth){
        this.url = url;
        this.sponsor = sponsor;
        this.idToken = idToken;
        this.documentCsv = documentCsv;
        this.numMonth = numMonth;
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

    public boolean isDocumentCsv() {
        return documentCsv;
    }

    public String getNumMonth() {
        return numMonth;
    }
}
