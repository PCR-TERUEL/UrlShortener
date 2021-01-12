package urlshortener.service.Tasks;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import urlshortener.domain.MetricQueueMessage;
import urlshortener.repository.impl.MetricsRepositoryImpl;


@RabbitListener(queues = TaskQueueService.METRIC_RESPONSE_QUEUE)
public class MetricsResponseListener {

    @Autowired
    private MetricsRepositoryImpl metricsRepository;


    @RabbitHandler
    public void receive(String in) {
        System.out.println(" [x] Received Metric'" + in + "'");
        MetricQueueMessage message = new MetricQueueMessage(in);

        metricsRepository.addMetric(message.getIdUser(), message.getMetrics());
    }
}
