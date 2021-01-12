package urlshortener.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


import java.net.URI;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class SystemTests {

  @Autowired
  private TestRestTemplate restTemplate;

  @LocalServerPort
  private int port;

  @Test
  public void testLogin() {
    ResponseEntity<String> entity = restTemplate.getForEntity("/login", String.class);
    assertThat(entity.getStatusCode(), is(HttpStatus.OK));
    assertNotNull(entity.getHeaders().getContentType());
    assertTrue(
            entity.getHeaders().getContentType().isCompatibleWith(new MediaType("text", "html")));
    assertThat(entity.getBody(), containsString("<h1>¿Ya estás registrado?</h1>"));
  }

  @Test
  public void testPanelNotLoggedIn() {
    ResponseEntity<String> entity = restTemplate.getForEntity("/panel", String.class);
    assertThat(entity.getStatusCode(), is(HttpStatus.FOUND));
    System.out.println(entity.getHeaders().getLocation());
    assertEquals(entity.getHeaders().getLocation(), URI.create("http://localhost:" + this.port + "/login"));
  }

  @Test
  public void testCss() {
    List<String> cssFiles = List.of("login.css", "style.css");

    ResponseEntity<String> entity;

    for (String cssFile : cssFiles) {
      entity = restTemplate.getForEntity("/css/" + cssFile, String.class);
      assertThat(entity.getStatusCode(), is(HttpStatus.OK));
      assertThat(entity.getHeaders().getContentType(), is(MediaType.valueOf("text/css")));
      assertThat(entity.getBody(), containsString("body"));
    }
  }

  @Test
  public void testImg() {
    List<String> images = List.of("about-img.jpg", "apple-touch-icon.png", "favicon.png", "features-1.svg",
                                  "header-bg.psd", "img-01.png", "intro-bg.jpg", "intro-img.svg", "team-1.jpg",
                                  "team-2.jpg", "team-3.jpg", "team-4.jpg", "testimonial-1.jpg", "testimonial-2.jpg",
                                  "testimonial-3.jpg", "testimonial-4.jpg", "why-us.jpg", "clients/client-1.png",
                                  "clients/client-2.png", "clients/client-3.png", "clients/client-4.png",
                                  "clients/client-5.png", "clients/client-6.png", "clients/client-7.png",
                                  "clients/client-8.png");


    ResponseEntity<String> entity;
    for (String image : images) {
      System.out.println("Checking: " + image);
      entity = restTemplate.getForEntity("/img/" + image, String.class);
      assertThat(entity.getStatusCode(), is(HttpStatus.OK));
    }

  }


}