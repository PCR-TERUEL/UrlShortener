package urlshortener.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import urlshortener.domain.User;
import urlshortener.repository.impl.UserRepositoryImpl;

import static org.junit.Assert.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;
import static urlshortener.fixtures.UserFixture.*;

public class SecureUserRepositoryTests {

  private EmbeddedDatabase db;
  private UserRepository repository;
  private JdbcTemplate jdbc;

  @Before
  public void setup() {
    db = new EmbeddedDatabaseBuilder().setType(HSQL)
        .addScript("schema-hsqldb.sql").build();
    jdbc = new JdbcTemplate(db);
    repository = new UserRepositoryImpl(jdbc);
  }

  @Test
  public void thatSavePersistsTheUser() {
    assertNotNull(repository.save(user1()));
    assertSame(jdbc.queryForObject("select count(*) from USER",
        Integer.class), 1);
  }

  @Test
  public void thatSaveRole() {
    assertNotNull(repository.save(userRole()));
    assertEquals(jdbc.queryForObject("select role from USER",
        String.class), "1");
  }

  @Test
  public void thatSaveADuplicateUserIsSafelyIgnored() {
    repository.save(userDuplicated());
    assertFalse(repository.save(userDuplicated()));
    assertSame(jdbc.queryForObject("select count(*) from USER",
        Integer.class), 1);
  }

  @Test
  public void thatFindByUserReturnsAUser() {
    repository.save(userFind());
    User u = repository.getUser(userFind().getUsername());
    assertNotNull(u);
    assertSame(u.getUsername(), userFind().getUsername());
  }

  @Test
  public void thatFindByUserReturnsNullWhenFails() {
    assertNull(repository.getUser(userNotExists().getUsername()));
  }

  @After
  public void shutdown() {
    db.shutdown();
  }

}
