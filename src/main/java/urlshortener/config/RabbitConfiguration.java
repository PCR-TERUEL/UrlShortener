package urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import urlshortener.service.TaskQueueService;

@Configuration
@ComponentScan("urlshortener.service")
public class RabbitConfiguration {

    @Bean
    public Queue tasks() {
        return new Queue("tasks");
    }
    @Bean
    public TaskQueueService taskQueueService(){return new TaskQueueService();}
}
