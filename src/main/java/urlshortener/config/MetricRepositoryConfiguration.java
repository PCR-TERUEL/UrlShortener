package urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import urlshortener.repository.impl.MetricsRepository;
import urlshortener.service.VallidationPeriodicTaskService;

import java.util.LinkedHashMap;

@Configuration
@EnableScheduling
public class MetricRepositoryConfiguration {

    @Bean
    public MetricsRepository metric(){
        return new MetricsRepository(new LinkedHashMap<>());
    }
    @Bean
    public VallidationPeriodicTaskService crono(){
        return new VallidationPeriodicTaskService();
    }
}
