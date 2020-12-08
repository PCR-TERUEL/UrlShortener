package urlshortener.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener.fixtures.ShortURLFixture.someUrl;


import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.domain.ShortURL;
import urlshortener.domain.User;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.service.UserService;

public class UrlShortenerTests {

  private MockMvc mockMvc;

  @Mock
  private ClickService clickService;

  @Mock
  private ShortURLService shortUrlService;

  @Mock
  private UserService userService;

  @InjectMocks
  private UrlShortenerController urlShortener;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
  }

  @Test
  public void thatRegisterIsSuccessful() throws Exception {
    configureUserSave("testUserxxx", "testPassword");

    mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
            .param("username","testUserxxx")
            .param("password","testPassword"))
            .andDo(print())
            .andExpect(status().isCreated());
  }


  @Test
  public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
    configureUrlSave(null);

    mockMvc.perform(post("/link")
            .param("url", "http://example.com/")
            .param("uuid","0"))
            .andDo(print())
            .andExpect(redirectedUrl("http://localhost/f684a3c4"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.hash", is("f684a3c4")))
            .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
            .andExpect(jsonPath("$.target", is("http://example.com/")))
            .andExpect(jsonPath("$.sponsor", is(nullValue())));
  }

  @Test
  public void thatShortenerCreatesARedirectIfTheURLisOK2() throws Exception {
    configureUrlSave(null);

    mockMvc.perform(post("/link")
            .param("url", "http://example.com/"))
            .andDo(print())
            .andExpect(redirectedUrl("http://localhost/f684a3c4"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.hash", is("f684a3c4")))
            .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
            .andExpect(jsonPath("$.target", is("http://example.com/")))
            .andExpect(jsonPath("$.sponsor", is(nullValue())));
  }


  @Test
  public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
      throws Exception {
    when(shortUrlService.findByKey("someKey")).thenReturn(someUrl());

    mockMvc.perform(get("/{id}", "someKey")).andDo(print())
        .andExpect(status().isTemporaryRedirect())
        .andExpect(redirectedUrl("http://example.com/"));
  }

  @Test
  public void thatRedirecToReturnsNotFoundIdIfKeyDoesNotExist()
      throws Exception {
    when(shortUrlService.findByKey("someKey")).thenReturn(null);

    mockMvc.perform(get("/{id}", "someKey")).andDo(print())
        .andExpect(status().isNotFound());
  }



  @Test
  public void thatShortenerCreatesARedirectWithSponsor() throws Exception {
    configureUrlSave("http://sponsor.com/");

    mockMvc.perform(
        post("/link").param("url", "http://example.com/").param(
            "sponsor", "http://sponsor.com/").param("uuid","0")).andDo(print())
        .andExpect(redirectedUrl("http://localhost/f684a3c4"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.hash", is("f684a3c4")))
        .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
        .andExpect(jsonPath("$.target", is("http://example.com/")))
        .andExpect(jsonPath("$.sponsor", is("http://sponsor.com/")));
  }

  @Test
  public void thatShortenerFailsIfTheURLisWrong() throws Exception {
    configureUrlSave(null);

    mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void thatShortenerFailsIfTheRepositoryReturnsNull() throws Exception {
    when(shortUrlService.save(any(String.class), any(String.class), any(String.class), any(String.class)))
        .thenReturn(null);

    mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
        .andExpect(status().isBadRequest());
  }

  private void configureUrlSave(String sponsor) {
    when(shortUrlService.save(any(),  any(), any(), any()))
        .then((Answer<ShortURL>) invocation -> new ShortURL(
            "f684a3c4",
            "http://example.com/",
            URI.create("http://localhost/f684a3c4"),
            sponsor,
            new Date(System.currentTimeMillis()),
            getExpirationDate(),
            null,
            0,
            false,
            null,
            null));
  }

  private void configureUserSave(String username, String password) {
    when(userService.save(any(),  any()))
            .then((Answer<User>) invocation -> new User("0", username, password,1)); // OJO!! Lo he tocado para hacer el apaño, estará mal.
  }


  private Date getExpirationDate() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    return new Date(calendar.getTimeInMillis());
  }
}
