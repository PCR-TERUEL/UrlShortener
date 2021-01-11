package urlshortener.domain;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MetricQueueMessage {
    public static final String ID_USER_FIELD_NAME = "idUser";
    public static final String METRICS_FIELD_NAME = "metrics";
    List<Metric> metrics;
    String idUser;

    public MetricQueueMessage(String in) {
        JSONParser jsonParser = new JSONParser();
        this.metrics = new ArrayList<>();
        try (StringReader reader = new StringReader(in))
        {
            Object obj = jsonParser.parse(reader);
            JSONObject message = (JSONObject) obj;
            this.idUser = (String) message.get(ID_USER_FIELD_NAME);
            JSONArray metricList = (JSONArray) message.get(METRICS_FIELD_NAME);
            for(Object metric : metricList){
                JSONObject o = (JSONObject) metric;
                metrics.add(new Metric(o));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public MetricQueueMessage(String in, List<Metric> m) {
        this.idUser = in;
        this.metrics = m;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("{\"idUser\":\"").append(idUser).append("\",");
        s.append("\"metrics\":[");
        for(int i = 0; i < metrics.size(); i++){
            s.append(metrics.get(i));
            if(i != (metrics.size() - 1))
                s.append(",");
        }

        s.append("]}");
        return s.toString();
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public String getIdUser() {
        return idUser;
    }

}
