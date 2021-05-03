package net.uweeisele.metrics;

public interface MetricsBinder<T> {

    void monitor(T target, String targetName);
}
