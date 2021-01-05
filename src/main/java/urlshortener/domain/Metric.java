package urlshortener.domain;

import org.json.simple.JSONObject;

public class Metric {


    boolean valid;
    String shortedUrl;
    String url;
    int clicks;

    public Metric(JSONObject o) {
        this.valid = o.get("valid").toString().equals("true");
        this.shortedUrl = o.get("shortedUrl").toString();
        this.url = o.get("url").toString();
        this.clicks = Integer.parseInt(o.get("clicks").toString());
    }

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("valid", this.valid);
        obj.put("shortedUrl", this.shortedUrl);
        obj.put("url", this.url);
        obj.put("clicks", this.clicks);

        return obj.toString();
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getShortedUrl() {
        return shortedUrl;
    }

    public void setShortedUrl(String shortedUrl) {
        this.shortedUrl = shortedUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
}
