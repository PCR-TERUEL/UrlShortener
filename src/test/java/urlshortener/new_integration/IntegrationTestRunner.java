package urlshortener.new_integration;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.Test;
import urlshortener.Application;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"},
                features = "src/test/java/urlshortener/new_integration/",
                glue = {"urlshortener.new_integration"},
                monochrome = true)
public class IntegrationTestRunner {

    @Test
    public void dummyTest(){}

}