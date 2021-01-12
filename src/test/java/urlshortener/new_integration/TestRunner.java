package urlshortener.new_integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import urlshortener.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)

public class TestRunner {

    @Test
    public void contextLoads() {
    }

}