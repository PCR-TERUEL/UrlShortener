package urlshortener.web;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

  @RunWith(SpringRunner.class)
  @SpringBootTest
  @EnableAutoConfiguration
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

    @WithMockUser("user")
    @Test
    public void thatGetsAllUserLinks() throws Exception {
      String accessToken = obtainAccessToken("user", "1234");
      mvc.perform(get("/userlinks").header("Authorization", "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @WithMockUser("user")
    @Test
    public void thatDoesSomethingElse() throws Exception {
      String accessToken = obtainAccessToken("user", "1234");
      mvc.perform(get("/users-information").header("Authorization", "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(content().contentType("application/json;charset=UTF-8"))
              .andExpect(jsonPath("$.token", is(notNullValue())));

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
