package de.novatec.http11.delegate.support.metrics;

public interface MetricsBinder<T> {

    void monitor(T target, String targetName);
}
