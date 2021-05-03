package net.uweeisele.support.metrics;

public interface MetricsBinder<T> {

    void monitor(T target, String targetName);
}
