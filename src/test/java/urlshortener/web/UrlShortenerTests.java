package urlshortener.web;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static urlshortener.fixtures.ShortURLFixture.someUrl;


import java.net.URI;
import java.sql.Date;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import urlshortener.Application;
import urlshortener.config.JWTTokenUtil;
import urlshortener.config.WebSecurityConfiguration;
import urlshortener.domain.JWT;
import urlshortener.domain.ShortURL;
import urlshortener.domain.User;
import urlshortener.service.ClickService;
import urlshortener.service.SecureUserService;
import urlshortener.service.ShortURLService;

  @RunWith(SpringRunner.class)
  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
  public class UrlShortenerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
      mvc = MockMvcBuilders
              .webAppContextSetup(context)
              .apply(springSecurity())
              .build();
    }


    @WithMockUser("spring")
    @Test
    public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
      mvc.perform(get("/private/hello").contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @WithMockUser("user")
    @Test
    public void thatDoSomethingPlease() throws Exception {
      String accessToken = obtainAccessToken("user", "1234");
      mvc.perform(get("/userlinks").header("Authorization", "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    private String obtainAccessToken(String username, String password) throws Exception {

      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      params.add("username", username);
      params.add("password", password);

      ResultActions result
              = mvc.perform(post("/authenticate")
              .params(params)
              .accept("application/json;charset=UTF-8"))
              .andExpect(status().isOk())
              .andExpect(content().contentType("application/json;charset=UTF-8"));

      String resultString = result.andReturn().getResponse().getContentAsString();

      JacksonJsonParser jsonParser = new JacksonJsonParser();
      return jsonParser.parseMap(resultString).get("token").toString();
    }
  }
