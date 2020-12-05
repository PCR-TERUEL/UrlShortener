package urlshortener.domain;

import java.util.Set;

public class User {

  private long id;
  private String username;
  private String password;
  private int roleId;
  private Set<Role> roles;

  public User(String username, String password, int roleId) {
    this.username = username;
    this.password = password;
    this.roleId = roleId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getRoleId() {
    return roleId;
  }

  public void setRoleId(int roleId) {
    this.roleId = roleId;
  }
}
