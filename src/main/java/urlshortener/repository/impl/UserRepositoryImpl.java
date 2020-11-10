package urlshortener.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import urlshortener.domain.User;
import urlshortener.repository.UserRepository;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;


@Repository
public class UserRepositoryImpl implements UserRepository {

  private static final Logger log = LoggerFactory
      .getLogger(UserRepositoryImpl.class);

  private static final RowMapper<User> rowMapper =
      (rs, rowNum) -> new User(rs.getString("username"), rs.getString("password"));

  private final JdbcTemplate jdbc;

  public UserRepositoryImpl(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }


  @Override
  public User save(User u) {
    try {
      jdbc.update("INSERT INTO user VALUES (?, ?,?)", null, u.getUsername(), u.getPassword());

    } catch (DuplicateKeyException e) {
      log.debug("When insert for key {}", u.getUsername(), e);
      return null;
    } catch (Exception e) {
      log.debug("When insert", e);
      return null;
    }
    return u;
  }


  @Override
  public Long count() {
    try {
      return jdbc
          .queryForObject("select count(*) from click", Long.class);
    } catch (Exception e) {
      log.debug("When counting", e);
    }
    return -1L;
  }

  @Override
  public List<User> list(Long limit, Long offset) {
    try {
      return jdbc.query("SELECT * FROM user LIMIT ? OFFSET ?",
          new Object[] {limit, offset}, rowMapper);
    } catch (Exception e) {
      log.debug("When select for limit " + limit + " and offset "
          + offset, e);
      return Collections.emptyList();
    }
  }


}