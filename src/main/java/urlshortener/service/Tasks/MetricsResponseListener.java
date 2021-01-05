package urlshortener.service.Tasks;

import net.minidev.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import urlshortener.web.UrlShortenerSocketController;

@RabbitListener(queues = TaskQueueService.VALIDATION_RESPONSE_QUEUE)
public class MetricsResponseListener {
    @Autowired
    private UrlShortenerSocketController urlShortenerSocketController;


    @RabbitHandler
    public void receive(String in) {
        System.out.println(" [x] Received Metric'" + in + "'");
        JSONObject employeeObject = (JSONObject) employee.get("employee");


        String message[] = in.split(TaskQueueService.SEPARATOR);
        String sessionId = message[0];
        String shortedURL = message[1];
        boolean valid = message[2].equals("true");
        String url = message[3];
        boolean isCSV = message[4].equals("true");
        urlShortenerSocketController.sendValidation(shortedURL, valid ,sessionId, url, isCSV);

    }
}
