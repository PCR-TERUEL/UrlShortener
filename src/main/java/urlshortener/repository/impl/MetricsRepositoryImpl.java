package urlshortener.repository.impl;

import urlshortener.domain.Metric;
import urlshortener.repository.MetricsRepository;

import java.util.LinkedHashMap;
import java.util.List;

public class MetricsRepositoryImpl implements MetricsRepository {
private LinkedHashMap<String, List<Metric>> metrics;

    public MetricsRepositoryImpl(LinkedHashMap<String, List<Metric>> metrics) {
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
