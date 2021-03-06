package urlshortener.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;
import static urlshortener.fixtures.ShortURLFixture.url1;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import urlshortener.domain.Click;
import urlshortener.fixtures.ClickFixture;
import urlshortener.fixtures.ShortURLFixture;
import urlshortener.repository.impl.ClickRepositoryImpl;
import urlshortener.repository.impl.ShortURLRepositoryImpl;
import urlshortener.repository.impl.UserRepositoryImpl;

public class ClickRepositoryTests {

  private EmbeddedDatabase db;
  private ClickRepository repository;
  private JdbcTemplate jdbc;

  @Before
  public void setup() {
    db = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName("testDB;MODE=MySQL")
            .addScript("bischema-mysql.sql").build();
    jdbc = new JdbcTemplate(db);
    ShortURLRepository shortUrlRepository = new ShortURLRepositoryImpl(jdbc);
    shortUrlRepository.save(url1());
    repository = new ClickRepositoryImpl(jdbc);
  }

  @Test
  public void thatSavePersistsTheClickURL() {
    Click click = repository.save(ClickFixture.click(url1()));
    assertSame(jdbc.queryForObject("select count(*) from CLICK",
        Integer.class), 1);
    assertNotNull(click);
    assertNotNull(click.getId());
  }

  @Test
  public void thatErrorsInSaveReturnsNull() {
    assertNull(repository.save(ClickFixture.click(ShortURLFixture.badUrl())));
    assertSame(jdbc.queryForObject("select count(*) from CLICK",
        Integer.class), 0);
  }

  @After
  public void shutdown() {
    db.shutdown();
  }

}
