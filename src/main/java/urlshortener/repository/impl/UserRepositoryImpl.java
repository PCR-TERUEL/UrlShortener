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

  private static final Logger log = LoggerFactory
      .getLogger(UserRepositoryImpl.class);

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
      jdbc.update("INSERT INTO user VALUES (?, ?,?)", null, u.getUsername(), u.getPassword());
      return true;
    } catch (DuplicateKeyException e) {
      log.debug("When insert for key {}", u.getUsername(), e);
    } catch (Exception e) {
      log.debug("When insert", e);
    }

    return false;
  }


  @Override
  public Long count() {
    try {
      return jdbc.queryForObject("select count(*) from click", Long.class);
    } catch (Exception e) {
      log.debug("When counting", e);
    }
    return -1L;
  }

  @Override
  public List<User> list(Long limit, Long offset) {
    try {
      return jdbc.query("SELECT * FROM user LIMIT ? OFFSET ?", new Object[] {limit, offset}, rowMapper);
    } catch (Exception e) {
      log.debug("When select for limit " + limit + " and offset "
          + offset, e);
      return Collections.emptyList();
    }
  }

  @Override
  public User login(User u) {
    List<User> users =  jdbc.query("SELECT * FROM user WHERE USERNAME = ? AND PASSWORD = ?",
            new Object[] {u.getUsername(), u.getPassword()}, rowMapper);

    if (!users.isEmpty()) {
      return users.get(0);
    }

    return null;
  }

  @Override
  public User getUser(String username) {
    try {
      return jdbc.query("SELECT * FROM user WHERE USERNAME = ?", new Object[] {username}, rowMapper).get(0);
    } catch (Exception e) {
      return null;
    }

  }

  @Override
  public boolean exists(String userId) {
    List<User> listUsers =  jdbc.query("SELECT * FROM USER WHERE ID = ?", new Object[] {userId}, rowMapper);
    return listUsers.size() > 0;
  }


}
