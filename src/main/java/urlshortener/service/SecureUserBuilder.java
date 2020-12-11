package urlshortener.service;

import urlshortener.domain.User;

public class SecureUserBuilder {
  private String id;
  private String username;
  private String password;
  private int roleId;

  static SecureUserBuilder newInstance() {
    return new SecureUserBuilder();
  }

  User build() {
    return new User(
        id,
        username,
        password,
        roleId
    );
  }

  SecureUserBuilder id(String id) {
    this.id = id;
    return this;
  }

  SecureUserBuilder username(String username) {
    this.username = username;
    return this;
  }

  SecureUserBuilder password(String password) {
    this.password = password;
    return this;
  }

  SecureUserBuilder roleId(int roleId) {
    this.roleId = roleId;
    return this;
  }

}
