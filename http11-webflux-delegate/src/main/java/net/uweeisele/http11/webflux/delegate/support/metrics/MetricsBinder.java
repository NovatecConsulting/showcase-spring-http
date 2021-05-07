package net.uweeisele.http11.webflux.delegate.support.metrics;

public interface MetricsBinder<T> {

    void monitor(T target, String targetName);
}
