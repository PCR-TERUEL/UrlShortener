package urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import urlshortener.repository.impl.MetricsRepositoryImpl;
import urlshortener.service.VallidationPeriodicTaskService;

import java.util.LinkedHashMap;

@Configuration
@EnableScheduling
public class MetricRepositoryConfiguration {

    @Bean
    public MetricsRepositoryImpl metric(){
        return new MetricsRepositoryImpl(new LinkedHashMap<>());
    }
    @Bean
    public VallidationPeriodicTaskService crono(){
        return new VallidationPeriodicTaskService();
    }
}
