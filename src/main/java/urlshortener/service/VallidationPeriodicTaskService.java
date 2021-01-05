package urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import urlshortener.domain.Metric;
import urlshortener.domain.MetricQueueMessage;
import urlshortener.repository.impl.MetricsRepository;
import urlshortener.service.Tasks.TaskQueueService;

import java.util.List;


public class VallidationPeriodicTaskService {
    int cont = 0;
    int last = -1;
    @Autowired
    private MetricsRepository metricsRepository;

    @Autowired
    private TaskQueueService taskQueueService;

    @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTask() {
        cont++;

        if(cont >= metricsRepository.getNumberOfItems())
            cont = 0;
        if(last != cont)
            taskQueueService.publishMetricJob(String.valueOf(cont));
        last = cont;
    }
}
