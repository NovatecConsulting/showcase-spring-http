package net.uweeisele.support.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolTaskExecutorMetricsBinder implements MetricsBinder<ThreadPoolTaskExecutor> {

    private final MeterRegistry meterRegistry;
    private final String metricPrefix;
    private final Iterable<Tag> tags;

    public ThreadPoolTaskExecutorMetricsBinder(MeterRegistry meterRegistry, String metricPrefix, Iterable<Tag> tags) {
        this.meterRegistry = meterRegistry;
        this.metricPrefix = metricPrefix;
        this.tags = tags;
    }

    /**
     * Adds micrometer metrics for the given taskExecutor. The taskExecutor must be initialized.
     * @throws IllegalStateException if taskExecutor is not initialized
     */
    @Override
    public void monitor(ThreadPoolTaskExecutor taskExecutor, String executorName) throws IllegalStateException {
        bindToMeterRegistry(taskExecutor.getThreadPoolExecutor(), executorName);
        registerTimedTaskDecorator(taskExecutor, executorName);
    }

    private void bindToMeterRegistry(ThreadPoolExecutor executor, String executorName) {
        new ExecutorServiceMetrics(
                executor,
                executorName,
                metricPrefix,
                tags)
                .bindTo(meterRegistry);
    }

    private void registerTimedTaskDecorator(ThreadPoolTaskExecutor taskExecutor, String executorName) {
        taskExecutor.setTaskDecorator(createTimedTaskDecorator(executorName));
    }

    private TimedTaskDecorator createTimedTaskDecorator(String executorName) {
        return new TimedTaskDecorator(
                meterRegistry,
                executorName,
                metricPrefix,
                tags);
    }
}
