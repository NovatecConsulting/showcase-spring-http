package de.novatec.http11.delegate.support.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.springframework.core.task.TaskDecorator;

public class TimedTaskDecorator implements TaskDecorator {

    private static final String DEFAULT_EXECUTOR_METRIC_PREFIX = "";

    private final MeterRegistry registry;
    private final Timer executionTimer;
    private final Timer idleTimer;

    public TimedTaskDecorator(MeterRegistry registry, String executorName, Iterable<Tag> tags) {
        this(registry, executorName, DEFAULT_EXECUTOR_METRIC_PREFIX, tags);
    }

    public TimedTaskDecorator(MeterRegistry registry, String executorName,
                                String metricPrefix, Iterable<Tag> tags) {
        this.registry = registry;
        final Tags finalTags = Tags.concat(tags, "name", executorName);
        this.executionTimer = registry.timer(metricPrefix + "executor", finalTags);
        this.idleTimer = registry.timer(metricPrefix + "executor.idle", finalTags);
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        return wrap(runnable);
    }

    private Runnable wrap(Runnable task) {
        return new TimedRunnable(registry, executionTimer, idleTimer, task);
    }
}
