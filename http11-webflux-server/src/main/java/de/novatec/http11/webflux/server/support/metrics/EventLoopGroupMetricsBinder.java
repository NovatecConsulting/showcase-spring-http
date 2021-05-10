package de.novatec.http11.webflux.server.support.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;

import java.util.logging.Logger;

import static java.util.Collections.emptySet;
import static java.util.logging.Level.WARNING;

public class EventLoopGroupMetricsBinder {

    private static final Logger log = Logger.getLogger(EventLoopGroupMetricsBinder.class.getName());

    private static final String DEFAULT_EVENT_EXECUTOR_METRIC_PREFIX = "";

    private final MeterRegistry meterRegistry;
    private final Iterable<Tag> tags;
    private final String metricPrefix;

    public EventLoopGroupMetricsBinder(MeterRegistry meterRegistry) {
        this(meterRegistry, emptySet());
    }

    public EventLoopGroupMetricsBinder(MeterRegistry meterRegistry, Iterable<Tag> tags) {
        this(meterRegistry, DEFAULT_EVENT_EXECUTOR_METRIC_PREFIX, tags);
    }

    public EventLoopGroupMetricsBinder(MeterRegistry meterRegistry, String metricPrefix, Iterable<Tag> tags) {
        this.meterRegistry = meterRegistry;
        this.metricPrefix = metricPrefix;
        this.tags = tags;
    }

    public void monitor(EventLoopGroup eventLoopGroup) {
        for (EventExecutor executor : eventLoopGroup) {
            monitor(executor);
        }
    }

    private void monitor(EventExecutor executor) {
        if (executor instanceof SingleThreadEventExecutor) {
            monitor((SingleThreadEventExecutor)executor);
        } else {
            log.log(WARNING, "Failed to bind as {} is unsupported.", executor.getClass().getName());
        }
    }
    private void monitor(SingleThreadEventExecutor executor) {
        Iterable<Tag> executorTags = Tags.concat(tags, "name", executor.threadProperties().name());

        Gauge.builder(metricPrefix + "executor.queued", executor, SingleThreadEventExecutor::pendingTasks)
                .tags(executorTags)
                .description("The number of tasks that are pending for processing.")
                .baseUnit(BaseUnits.TASKS)
                .register(meterRegistry);
    }
}
