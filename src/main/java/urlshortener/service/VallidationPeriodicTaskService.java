package urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import urlshortener.repository.impl.MetricsRepositoryImpl;
import urlshortener.service.Tasks.TaskQueueService;


public class VallidationPeriodicTaskService {
    int cont = 0;
    int last = -1;
    @Autowired
    private MetricsRepositoryImpl metricsRepository;

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
