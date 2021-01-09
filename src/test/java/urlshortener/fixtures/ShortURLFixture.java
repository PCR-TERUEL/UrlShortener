package urlshortener.fixtures;

import urlshortener.domain.ShortURL;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ShortURLFixture {

  public static ShortURL url1() {
    return new ShortURL("3j893rjf", "http://www.unizar.es/", null, null,null,  null, null, null, false,
        null, null);
  }

  public static ShortURL url2() {
    return new ShortURL("fafe48f", "http://www.unizar.es/", null, null,null,  null, null, null, false,
        null, null);
  }

  public static ShortURL badUrl() {
    return new ShortURL(null, null, null, null,null, null, null, null, false,
        null, null);
  }

  public static ShortURL expiredUrl() throws ParseException {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date actual = df.parse("2020-01-01");


    return new ShortURL("rf323t", "http://microsoft.com/", null, null,null, new Date(actual.getTime()), null, null, false,
            null, null);
  }

  public static ShortURL unexpiredUrl() throws ParseException {

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date actual = df.parse("2038-01-01");

    return new ShortURL("rc3rff2", "http://oracle.com/", null, null,null, new Date(actual.getTime()), null, null, false,
            null, null);
  }
  /*
  public static ShortURL urlSponsor() {
    return new ShortURL("3", null, null, "sponsor", null,null, null, null,
        false, null, null);
  }

  public static ShortURL urlSafe() {
    return new ShortURL("4", null, null, "sponsor", null,null, null, null, true,
        null, null);
  }

  public static ShortURL someUrl() {
    return new ShortURL("someKey", "http://example.com/", null,null, null, null,
        null, 307, true, null, null);
  }
  */

}
