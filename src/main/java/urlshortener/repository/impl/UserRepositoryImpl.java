package urlshortener.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import urlshortener.domain.User;
import urlshortener.repository.UserRepository;
import java.util.Collections;
import java.util.List;


@Repository
public class UserRepositoryImpl implements UserRepository {

  private static final RowMapper<User> rowMapper =
    (rs, rowNum) -> new User(rs.getString("id"), rs.getString("username"),
            rs.getString("password"), rs.getInt("role"));

  private final JdbcTemplate jdbc;

  public UserRepositoryImpl(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public boolean save(User u) {
    try {
      jdbc.update("INSERT INTO user(USERNAME, PASSWORD, ROLE) VALUES (?,?,?)",
              u.getUsername(), u.getPassword(), u.getRoleId());
      return true;
    }  catch (Exception e) {
      System.out.println(e);
    }

    return false;
  }

  @Override
  public Long count() {
    try {
      return jdbc.queryForObject("select count(*) from user", Long.class);
    } catch (Exception e) {
      System.out.println(e);
    }
    return -1L;
  }

  @Override
  public List<User> list(Long limit, Long offset) {
    try {
      return jdbc.query("SELECT * FROM user LIMIT ? OFFSET ?", new Object[] {limit, offset}, rowMapper);
    } catch (Exception e) {
      System.out.println();
      return Collections.emptyList();
    }
  }

  @Override
  public User getUser(String username) {
    List<User> users = jdbc.query("SELECT * FROM user WHERE USERNAME = ?", new Object[] {username}, rowMapper);
    if (users.isEmpty()) {
      return null;
    } else {
      return users.get(0);
    }
  }

  @Override
  public List<User> getUsers() {
    return jdbc.query("SELECT * FROM user", rowMapper);
  }

  @Override
  public boolean deleteById(int userId) {
    return jdbc.update("DELETE FROM user WHERE ID = ?", userId) == 1;
  }
}
