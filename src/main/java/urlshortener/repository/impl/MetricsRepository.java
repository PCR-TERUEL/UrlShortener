package urlshortener.repository.impl;

import urlshortener.domain.Metric;
import urlshortener.repository.MetricsRepositoryImpl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MetricsRepository implements MetricsRepositoryImpl {
private LinkedHashMap<String, List<Metric>> metrics;

    public MetricsRepository(LinkedHashMap<String, List<Metric>> metrics) {
        this.metrics = metrics;
    }

    public void addMetric(String idUser,List<Metric> m){
        this.metrics.put(idUser, m);
    }

    public List<Metric> getMetrics(String idUser){
        return this.metrics.get(idUser);
    }

    public int getNumberOfItems() {
        return metrics.size();
    }

    public boolean contains(String key) {
        return this.metrics.containsKey(key);
    }
}
