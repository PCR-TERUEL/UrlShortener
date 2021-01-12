package urlshortener.fixtures;

import urlshortener.domain.ShortURL;
import urlshortener.domain.User;

public class UserFixture {

  public static User user1() {
    return new User("1", "usertest", "1234", 2);
  }

  public static User userRole() {
    return new User("2", "role", "1234", 1);
  }

  public static User userDuplicated() {
    return new User("3", "userdup", "1234", 1);
  }
  public static User userDelete() {
    return new User("4", "delete", "1234", 1);
  }


  public static User userFind() {
    return new User("-1", "userfind", "1234", 1);
  }

  public static User userNotExists() {
    return new User("-1", "usernotexists", "1234", 1);
  }

  public static ShortURL url1modified() {
    return new ShortURL("1", "http://www.unizar.org/", null, null,null,  null, null, null, false,
        null, null);
  }

  public static ShortURL url2() {
    return new ShortURL("2", "http://www.unizar.es/", null, null,null,  null, null, null, false,
        null, null);
  }

  public static ShortURL url3() {
    return new ShortURL("3", "http://www.google.es/", null, null,null,  null, null, null, false,
        null, null);
  }

  public static ShortURL badUrl() {
    return new ShortURL(null, null, null, null,null, null, null, null, false,
        null, null);
  }

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
}
