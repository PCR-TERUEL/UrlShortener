package urlshortener.domain;


import javax.persistence.*;
import java.io.Serializable;

@Entity
public class User implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String id;
  private String username;
  private String password;
  private int roleId;

  public User(String id, String username, String password, int roleId) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.roleId = roleId;
  }

  public User() { }


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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getRoleId() {
    return roleId;
  }

  public void setRoleId(int roleId) {
    this.roleId = roleId;
  }
}
