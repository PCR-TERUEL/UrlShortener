package urlshortener.service.Tasks;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


public class TaskQueueService {
    public static final String VALIDATION_JOB_QUEUE = "validation_job_d";
    public static final String VALIDATION_RESPONSE_QUEUE = "validation_resp_d";
    public static final String REFRESH_JOB_QUEUE = "refresh_job";
    public static final String REFRESH_RESPONSE_QUEUE= "refresh_resp";
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

        System.out.println(" [x] Sent "+ tasks.getName() + "'" + message + "'");
    }
}
