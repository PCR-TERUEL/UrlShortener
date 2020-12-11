package urlshortener.web;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
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
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import urlshortener.domain.ShortURL;
import urlshortener.domain.User;
import urlshortener.service.ClickService;
import urlshortener.service.SecureUserService;
import urlshortener.service.ShortURLService;

public class UrlShortenerTests {

  private MockMvc mockMvc;

  @Mock
  private ClickService clickService;

  @Mock
  private ShortURLService shortUrlService;

  @Mock
  private SecureUserService userService;

  @InjectMocks
  private UrlShortenerController urlShortener;

  @Autowired // import through Spring
  private UrlShortenerController urlShortenerController;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
  }

  @Test
  public void thatRegisterIsSuccessful() throws Exception {
    configureUserSave("user", "1234");

    mockMvc.perform(post("/singup").contentType(MediaType.APPLICATION_JSON)
            .param("username","user")
            .param("password","1234"))
            .andDo(print())
            .andExpect(status().isCreated());
  }

  @Test
  public void thatAuthenticationIsSuccessful() throws Exception {
    MvcResult result = mockMvc.perform(post("/authenticate").contentType(MediaType.APPLICATION_JSON)
            .param("username","user")
            .param("password","1234"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("token", is(notNullValue()))).andReturn();

    System.out.println(result.getResponse().getContentAsString());

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
            .then((Answer<Boolean>) invocation -> true);
  }


  private Date getExpirationDate() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    return new Date(calendar.getTimeInMillis());
  }
}
