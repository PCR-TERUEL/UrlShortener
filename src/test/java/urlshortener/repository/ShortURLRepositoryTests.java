package urlshortener.repository;

import static org.junit.Assert.*;
import static urlshortener.fixtures.ShortURLFixture.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import urlshortener.domain.ShortURL;
import urlshortener.fixtures.ShortURLFixture;
import urlshortener.repository.impl.ShortURLRepositoryImpl;

import java.text.ParseException;
import java.util.List;

public class ShortURLRepositoryTests {

  private EmbeddedDatabase db;
  private ShortURLRepository repository;
  private JdbcTemplate jdbc;

  @Before
  public void setup() {
    db = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName("testDB;MODE=MySQL")
            .addScript("bischema-mysql.sql").build();
    jdbc = new JdbcTemplate(db);
    ShortURLRepository shortUrlRepository = new ShortURLRepositoryImpl(jdbc);
    //shortUrlRepository.save(ShortURLFixture.url1());
    //shortUrlRepository.save(ShortURLFixture.url2());
    repository = new ShortURLRepositoryImpl(jdbc);
  }

  @Test
  public void thatSavePersistsTheShortURL() {
    assertNotNull(repository.save(url1()));
    assertSame(jdbc.queryForObject("select count(*) from SHORTURL",
        Integer.class), 2);
  }

  @Test
  public void thatSaveADuplicateHashIsSafelyIgnored() {
    repository.save(url1());
    assertNull(repository.save(url1()));
    assertSame(jdbc.queryForObject("select count(*) from SHORTURL",
            Integer.class), 2);
  }

  @Test
  public void thatErrorsInSaveReturnsNull() {
    assertNull(repository.save(badUrl()));
    assertSame(jdbc.queryForObject("select count(*) from SHORTURL",
            Integer.class), 1);
  }

  @Test
  public void thatFindByKeyReturnsAURL() {
    repository.save(url1());
    ShortURL su = repository.findByKey(url1().getHash());
    assertNotNull(su);
    assertSame(su.getHash(), url1().getHash());
  }

  @Test
  public void thatFindByKeyReturnsNullWhenFails() {
    repository.save(url1());
    assertNull(repository.findByKey(url2().getHash()));
  }

  @Test
  public void thatFindByTargetReturnsURLs() {
    repository.save(url1());
    List<ShortURL> sul = repository.findByTarget(url1().getTarget());
    assertEquals(sul.size(), 1);
    sul = repository.findByTarget("dummy");
    assertEquals(sul.size(), 0);
  }

  @Test
  public void thatDeleteDelete() {
    repository.delete(url1().getHash());
    assertEquals(repository.count().intValue(), 1);
  }

  @Test
  public void thatExpiredUrlIsReturnsExpired() throws ParseException {
    repository.save(expiredUrl());
    assertEquals(repository.isExpired(expiredUrl().getHash()), true);
  }

  @Test
  public void thatUnexpiredUrlIsReturnsUnexpired() throws ParseException {
    repository.save(unexpiredUrl());
    assertEquals(repository.isExpired(expiredUrl().getHash()), false);
  }



  /*
    @Test
    public void thatSaveSafe() {
      assertNotNull(repository.save(urlSafe()));
      assertSame(
          jdbc.queryForObject("select safe from SHORTURL", Boolean.class),
          true);
      repository.mark(urlSafe(), false);
      assertSame(
          jdbc.queryForObject("select safe from SHORTURL", Boolean.class),
          false);
      repository.mark(urlSafe(), true);
      assertSame(
          jdbc.queryForObject("select safe from SHORTURL", Boolean.class),
          true);
    }

    @Test
    public void thatSaveADuplicateHashIsSafelyIgnored() {
      repository.save(url1());
      assertNotNull(repository.save(url1()));
      assertSame(jdbc.queryForObject("select count(*) from SHORTURL",
          Integer.class), 1);
    }

    @Test
    public void thatErrorsInSaveReturnsNull() {
      assertNull(repository.save(badUrl()));
      assertSame(jdbc.queryForObject("select count(*) from SHORTURL",
          Integer.class), 0);
    }

    @Test
    public void thatFindByKeyReturnsAURL() {
      repository.save(url1());
      repository.save(url2());
      ShortURL su = repository.findByKey(url1().getHash());
      assertNotNull(su);
      assertSame(su.getHash(), url1().getHash());
    }

    @Test
    public void thatFindByKeyReturnsNullWhenFails() {
      repository.save(url1());
      assertNull(repository.findByKey(url2().getHash()));
    }

    @Test
    public void thatFindByTargetReturnsURLs() {
      repository.save(url1());
      repository.save(url2());
      repository.save(url3());
      List<ShortURL> sul = repository.findByTarget(url1().getTarget());
      assertEquals(sul.size(), 2);
      sul = repository.findByTarget(url3().getTarget());
      assertEquals(sul.size(), 1);
      sul = repository.findByTarget("dummy");
      assertEquals(sul.size(), 0);
    }

    @Test
    public void thatDeleteDelete() {
      repository.save(url1());
      repository.save(url2());
      repository.delete(url1().getHash());
      assertEquals(repository.count().intValue(), 1);
      repository.delete(url2().getHash());
      assertEquals(repository.count().intValue(), 0);
    }

    @Test
    public void thatUpdateUpdate() {
      repository.save(url1());
      ShortURL su = repository.findByKey(url1().getHash());
      assertEquals(su.getTarget(), "http://www.unizar.es/");
      repository.update(url1modified());
      su = repository.findByKey(url1().getHash());
      assertEquals(su.getTarget(), "http://www.unizar.org/");
    }
  */
  @After
  public void shutdown() {
    db.shutdown();
  }

}
