package urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import urlshortener.service.Tasks.MetricsResponseListener;
import urlshortener.service.Tasks.TaskQueueService;
import urlshortener.service.Tasks.ValidationResponseListener;

/**
 * Rabbit configuration declarations
 *
 */

@Configuration
@ComponentScan("urlshortener.service")
public class RabbitConfiguration {

    @Bean
    public Queue tasks() {
        return new Queue("tasks");
    }
    @Bean
    public TaskQueueService taskQueueService(){return new TaskQueueService();}
    @Bean
    public ValidationResponseListener validationReceiver() {
        return new ValidationResponseListener();
    }
    @Bean
    public MetricsResponseListener metricReceiver() {  return new MetricsResponseListener(); }
}
