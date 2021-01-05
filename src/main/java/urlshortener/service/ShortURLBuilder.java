package urlshortener.service;

import static com.google.common.hash.Hashing.murmur3_32;


import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Calendar;
import java.util.function.Function;
import org.springframework.http.HttpStatus;
import urlshortener.domain.ShortURL;

public class ShortURLBuilder {

  private String hash;
  private String target;
  private URI uri;
  private String sponsor;
  private Date created;
  private Date expiration;
  private Long owner;
  private Integer mode;
  private Boolean safe;
  private String ip;
  private String country;

  static ShortURLBuilder newInstance() {
    return new ShortURLBuilder();
  }

  ShortURL build() {
    return new ShortURL(
        hash,
        target,
        uri,
        sponsor,
        created,
        expiration,
        owner,
        mode,
        safe,
        ip,
        country
    );
  }

  ShortURLBuilder target(String url, String owner) {
    target = url;
    //noinspection UnstableApiUsage
    hash = murmur3_32().hashString(url + owner, StandardCharsets.UTF_8).toString();
    return this;
  }

  ShortURLBuilder sponsor(String sponsor) {
    this.sponsor = sponsor;
    return this;
  }

  ShortURLBuilder createdNow() {
    this.created = new Date(System.currentTimeMillis());
    return this;
  }

  ShortURLBuilder addDateExpiration(int numMonth) {
    if(numMonth == -1){
      this.expiration = new Date(0);
    } else {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.MONTH, numMonth);
      this.expiration = new Date(calendar.getTimeInMillis());
    }

    System.out.println("El numMonth es -> " + numMonth + " y la fecha guardada es ->" + expiration);
    return this;
  }

  ShortURLBuilder owner(String owner) {
    this.owner = Long.parseLong(owner);
    return this;
  }

  ShortURLBuilder temporaryRedirect() {
    this.mode = HttpStatus.TEMPORARY_REDIRECT.value();
    return this;
  }

  ShortURLBuilder treatAsSafe() {
    this.safe = true;
    return this;
  }

  ShortURLBuilder ip(String ip) {
    this.ip = ip;
    return this;
  }

  ShortURLBuilder unknownCountry() {
    this.country = null;
    return this;
  }

  ShortURLBuilder uri(Function<String, URI> extractor) {
    this.uri = extractor.apply(hash);
    return this;
  }
}
