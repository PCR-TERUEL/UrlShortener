package urlshortener.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import urlshortener.domain.User;
import urlshortener.repository.impl.ShortURLRepositoryImpl;
import urlshortener.repository.impl.UserRepositoryImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;
import static urlshortener.fixtures.UserFixture.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRepositoryTests {

  private EmbeddedDatabase db;
  private UserRepository repository;
  private JdbcTemplate jdbc;

  @Before
  public void setup() {
    db = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName("testDB;MODE=MySQL")
            .addScript("bischema-mysql.sql").build();
    jdbc = new JdbcTemplate(db);
    ShortURLRepository shortUrlRepository = new ShortURLRepositoryImpl(jdbc);
    repository = new UserRepositoryImpl(jdbc);
  }

  @Test
  public void thatSavePersistsTheUser() {
    int nUsersBefore = jdbc.queryForObject("select count(*) from USER",
            Integer.class);
    assertNotNull(repository.save(user1()));
    assertSame(jdbc.queryForObject("select count(*) from USER",
        Integer.class), nUsersBefore + 1);
  }

  @Test
  public void thatSaveRole() {
    System.out.println("aver" + repository.getUsers());
    assertNotNull(repository.save(userRole()));
    assertSame(jdbc.queryForObject("select role from USER",
        Integer.class), 1);
  }

  @Test
  public void thatSaveADuplicateUserIsSafelyIgnored() {
    System.out.println("aver" + repository.getUsers());
    repository.save(userDuplicated());
    int nUsersBefore = jdbc.queryForObject("select count(*) from USER",
            Integer.class);
    assertFalse(repository.save(userDuplicated()));
    assertSame(jdbc.queryForObject("select count(*) from USER",
        Integer.class), nUsersBefore);
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

  @Test
  public void thatCountCounts() {
    repository.save(user1());
    repository.save(userRole());
    System.out.println("aver0"+repository.getUsers());
    assertSame(2L, repository.count());
  }

  @Test
  public void thatGetUsersReturnsAllUsers() {
    repository.save(user1());
    repository.save(userRole());
    List<User> users = new ArrayList<>();
    users.add(user1());
    users.add(userRole());
    assertEquals(users.toString(), repository.getUsers().toString());
  }

  @Test
  public void thatListReturnUserLists() {
    List<User> users = new ArrayList<>();
    users.add(user1());
    users.add(userRole());
    repository.save(user1());
    repository.save(userRole());
    assertEquals(users.toString(),repository.list(2L, 0L).toString());
  }

  @Test
  public void thatListReturnEmptyList() {
    assertEquals(repository.list(9999L, 99999L), Collections.emptyList());
  }


  @Test
  public void thatDeleteDeletes() {
    repository.save(userDelete());
    assertEquals(true, repository.deleteById(1));
    assertNull(repository.getUser("delete"));
  }

  @Test
  public void thatNonExistingUserDeleteReturnsNull() {
    assertEquals(false, repository.deleteById(99999));
    assertNull(repository.getUser("delete"));
  }

  @After
  public void shutdown() {
    db.shutdown();
  }

}
