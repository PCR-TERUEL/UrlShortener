package urlshortener.config;

import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import urlshortener.repository.ClickRepository;
import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.UserRepository;
import urlshortener.repository.impl.ClickRepositoryImpl;
import urlshortener.repository.impl.ShortURLRepositoryImpl;
import urlshortener.repository.impl.UserRepositoryImpl;

@Configuration
@EnableWebMvc
public class PersistenceConfiguration implements WebMvcConfigurer {

  private final JdbcTemplate jdbc;
  private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
          "classpath:/META-INF/resources/", "classpath:/resources/",
          "classpath:/static/", "classpath:/public/" };

  public PersistenceConfiguration(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Bean
  ShortURLRepository shortURLRepository() {
    return new ShortURLRepositoryImpl(jdbc);
  }

  @Bean
  ClickRepository clickRepository() {
    return new ClickRepositoryImpl(jdbc);
  }

  @Bean
  UserRepository userRepository() {
    return new UserRepositoryImpl(jdbc);
  }

  /**
   * Enable URL mapping for webjars
   *
   * @param registry
   */

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!registry.hasMappingForPattern("/webjars/**")) {
      registry.addResourceHandler("/webjars/**").addResourceLocations(
              "classpath:/META-INF/resources/webjars/");
    }
    if (!registry.hasMappingForPattern("/**")) {
      registry.addResourceHandler("/**").addResourceLocations(
              CLASSPATH_RESOURCE_LOCATIONS );
    }
  }

  /**
   * Enable URL mapping for static content
   *
   * @return
   */

  @Bean
  public InternalResourceViewResolver internalResourceViewResolver() {
    InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
    internalResourceViewResolver.setSuffix(".html");
    return internalResourceViewResolver;
  }

  /**
   * Add URL mapping for some URL's (probably deprecated)
   *
   * @param registry
   */

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("index");
    registry.addViewController("/panel").setViewName("panel");
    registry.addViewController("/error").setViewName("error");
  }

  /**
   * Set legacy cookie processor for cookie handling
   *
   * @return
   */

  @Bean
  public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
    return (serverFactory) -> serverFactory.addContextCustomizers(
            (context) -> context.setCookieProcessor(new LegacyCookieProcessor()));
  }

}
