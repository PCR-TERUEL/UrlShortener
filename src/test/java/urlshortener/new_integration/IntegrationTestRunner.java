package urlshortener.new_integration;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import urlshortener.Application;

import java.nio.file.Watchable;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"},
                features = "src/test/java/urlshortener/new_integration/",
                glue = {"urlshortener.new_integration"},
                monochrome = true)
@SpringBootTest(classes = { Application.class })
public class IntegrationTestRunner {

    @Test
    public void dummyTest(){}

}