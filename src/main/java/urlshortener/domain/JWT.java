package urlshortener.domain;

import java.io.Serializable;

public class JWT implements Serializable {

    private String token;

    public JWT(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
