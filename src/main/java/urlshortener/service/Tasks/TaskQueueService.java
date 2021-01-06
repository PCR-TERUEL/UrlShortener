package urlshortener.service.Tasks;

import org.json.simple.JSONObject;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import urlshortener.domain.MetricQueueMessage;


public class TaskQueueService {
    public static final String VALIDATION_JOB_QUEUE = "validation_job_d";
    public static final String VALIDATION_RESPONSE_QUEUE = "validation_resp_d";
    public static final String METRIC_JOB_QUEUE = "metric_job";
    public static final String METRIC_RESPONSE_QUEUE= "metric_resp";
    public static final String SEPARATOR= "@";
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue tasks;

    public void send(String queue, String message) {
        this.template.convertAndSend(tasks.getName(), message);
        System.out.println();
        System.out.println(" [x] Sent "+ tasks.getName() + "'" + message + "'");
    }

    public void publishValidationJob(String sessionId, String url, String shortedURL, boolean isCSV) {
        String message =  sessionId + SEPARATOR + url + SEPARATOR + shortedURL + SEPARATOR + isCSV;
        this.template.convertAndSend(VALIDATION_JOB_QUEUE, message);

        System.out.println(" [x] Sent VALIDATION"+ tasks.getName() + "'" + message + "'");
    }

    public void publishMetricJob(String idUser) {
        JSONObject obj = new JSONObject();
        obj.put(MetricQueueMessage.ID_USER_FIELD_NAME, idUser);
        String message =  obj.toString();
        this.template.convertAndSend(METRIC_JOB_QUEUE, message);

        System.out.println(" [x] Sent METRIC "+ tasks.getName() + "'" + message + "'");
    }
}
