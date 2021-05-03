package net.uweeisele.support.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class MetricsTaskExecutorCustomizer implements TaskExecutorCustomizer {

    private final MeterRegistry meterRegistry;
    private final String metricPrefix;
    private final Iterable<Tag> tags;

    public MetricsTaskExecutorCustomizer(MeterRegistry meterRegistry, String metricPrefix, Iterable<Tag> tags) {
        this.meterRegistry = meterRegistry;
        this.metricPrefix = metricPrefix;
        this.tags = tags;
    }

    @Override
    public void customize(ThreadPoolTaskExecutor taskExecutor) {
        customize(taskExecutor, sanitizeName(taskExecutor.getThreadNamePrefix()));
    }

    public void customize(ThreadPoolTaskExecutor taskExecutor, String executorName) throws IllegalStateException {
        bindToMeterRegistry(taskExecutor, executorName);
        registerTimedTaskDecorator(taskExecutor, executorName);
    }

    private void bindToMeterRegistry(ThreadPoolTaskExecutor executor, String executorName) {
        new TaskExecutorMetricsBinder(meterRegistry, tags).monitor(executor, executorName);
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

    private static String sanitizeName(String name) {
        if (StringUtils.isBlank(name))
            return "";
        if (name.endsWith("-"))
            return name.substring(0, name.length() - 1);
        return name;
    }
}
