package urlshortener.repository;

import urlshortener.domain.Metric;

import java.util.LinkedHashMap;
import java.util.List;

public interface MetricsRepositoryImpl {

    public void addMetric(String idUser,List<Metric> m);

    public List<Metric> getMetrics(String idUser);

    public int getNumberOfItems();

    public boolean contains(String key);
}
