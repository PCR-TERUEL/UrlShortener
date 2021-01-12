package urlshortener.web;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

    @Test
    public void thatSingupIsSuccessful() throws Exception {
      mvc.perform(post("/singup")
              .contentType(MediaType.APPLICATION_JSON)
              .param("username", "test")
              .param("password","1234"))
              .andExpect(status().isCreated());
    }

    @Test
    public void thatSingupIsUnsuccessful() throws Exception {
      mvc.perform(post("/singup")
              .contentType(MediaType.APPLICATION_JSON)
              .param("username", "user")
              .param("password","1234"))
              .andExpect(status().isConflict());
    }

    @Test
    public void thatSingupHasBadParams() throws Exception {
      mvc.perform(post("/singup")
              .contentType(MediaType.APPLICATION_JSON)
              .param("username", "user"))
              .andExpect(status().isBadRequest());
    }

    @Test
    public void thatAuthenticationIsSuccessful() throws Exception {
      mvc.perform(post("/authenticate")
              .contentType(MediaType.APPLICATION_JSON)
              .param("username", "user")
              .param("password","1234"))
              .andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.token", is(notNullValue())));
    }

    @Test
    public void thatAuthenticationIsUnsuccessful() throws Exception {
      mvc.perform(post("/authenticate")
              .contentType(MediaType.APPLICATION_JSON)
              .param("username", "not_exists")
              .param("password","1234"))
              .andExpect(status().isNotFound());
    }

    @Test
    public void thatAuthenticationHasBadParams() throws Exception {
      mvc.perform(post("/authenticate")
              .contentType(MediaType.APPLICATION_JSON)
              .param("username", "not_exists"))
              .andExpect(status().isBadRequest());
    }

    @WithMockUser("user")
    @Test
    public void thatGetUserLinksIsSuccessful() throws Exception {
      mvc.perform(get("/userlinks")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.length()", is(1)))
              .andExpect(jsonPath("$.urlList[0].uri", containsString("09bb6428")))
              .andExpect(jsonPath("$.urlList[0].target", is("https://www.live.com")))
              .andExpect(jsonPath("$.urlList[0].clicks", is(0)))
              .andExpect(jsonPath("$.urlList[0].valid", is(true)));
    }

    @Test
    public void thatGetUserLinksIsUnsuccessful() throws Exception {
      mvc.perform(get("/userlinks")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isFound());
    }

    @WithMockUser("admin")
    @Test
    public void thatGetUsersInformationIsSuccessful() throws Exception {
      mvc.perform(post("/users-information")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.length()", is(3)))
              .andExpect(jsonPath("$[0].id", is("1")))
              .andExpect(jsonPath("$[0].username", is("admin")))
              .andExpect(jsonPath("$[0].password", is("1234")))
              .andExpect(jsonPath("$[0].roleId", is(1)))
              .andExpect(jsonPath("$[1].id", is("2")))
              .andExpect(jsonPath("$[1].username", is("user")))
              .andExpect(jsonPath("$[1].password", is("1234")))
              .andExpect(jsonPath("$[1].roleId", is(2)));

    }

    @Test
    public void thatGetUsersInformationIsForbidden() throws Exception {
      mvc.perform(post("/users-information")
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isFound());

    }


    @Test
  //  @WithMockUser("admin")
    public void thatDeleteUserIsSuccessful() throws Exception {
      mvc.perform(delete("/user/3")
              .header("Authorization", "Bearer " + getAccesToken("admin","1234")))
              .andExpect(status().isOk());
    }


    @Test
    @WithMockUser("user")
    public void thatDeleteUserIsUnsuccessful() throws Exception {
      mvc.perform(delete("/user/4")
              .header("Authorization", "Bearer " + getAccesToken("admin","1234")))
              .andExpect(status().isNotFound());
    }

    @Test
    public void thatDeleteUserIsForbidden() throws Exception {
      mvc.perform(delete("/user/2"))
              .andExpect(status().isFound());
    }

    private String getAccesToken(String username, String password) throws Exception {

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
