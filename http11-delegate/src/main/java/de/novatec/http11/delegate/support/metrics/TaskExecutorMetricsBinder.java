package de.novatec.http11.delegate.support.metrics;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Metrics binder for {@link ThreadPoolTaskExecutor}.
 * Based in {@link io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics},
 * which could not be used, because it requires that executor is initialized before the binding.
 */
public class TaskExecutorMetricsBinder {

    private static final String DEFAULT_EXECUTOR_METRIC_PREFIX = "";

    private final MeterRegistry registry;
    private final Iterable<Tag> tags;
    private final String metricPrefix;

    public TaskExecutorMetricsBinder(MeterRegistry registry, Iterable<Tag> tags) {
        this(registry, DEFAULT_EXECUTOR_METRIC_PREFIX, tags);
    }

    public TaskExecutorMetricsBinder(MeterRegistry registry, String metricPrefix, Iterable<Tag> tags) {
        this.registry = registry;
        this.tags = tags;
        this.metricPrefix = sanitizePrefix(metricPrefix);
    }

    public void monitor(ThreadPoolTaskExecutor executor, String executorName) {
        final Iterable<Tag> executorTags = Tags.concat(tags, "name", executorName);
        monitorThreadPoolTaskExecutor(executor, executorTags);
        monitorThreadPoolExecutor(executor, executorTags);
    }

    private void monitorThreadPoolTaskExecutor(ThreadPoolTaskExecutor tpte, Iterable<Tag> tags) {
        Gauge.builder(metricPrefix + "executor.active", tpte, ThreadPoolTaskExecutor::getActiveCount)
                .tags(tags)
                .description("The approximate number of threads that are actively executing tasks")
                .baseUnit(BaseUnits.THREADS)
                .register(registry);

        Gauge.builder(metricPrefix + "executor.pool.size", tpte, ThreadPoolTaskExecutor::getPoolSize)
                .tags(tags)
                .description("The current number of threads in the pool")
                .baseUnit(BaseUnits.THREADS)
                .register(registry);

        Gauge.builder(metricPrefix + "executor.pool.core", tpte, ThreadPoolTaskExecutor::getCorePoolSize)
                .tags(tags)
                .description("The core number of threads for the pool")
                .baseUnit(BaseUnits.THREADS)
                .register(registry);

        Gauge.builder(metricPrefix + "executor.pool.max", tpte, ThreadPoolTaskExecutor::getMaxPoolSize)
                .tags(tags)
                .description("The maximum allowed number of threads in the pool")
                .baseUnit(BaseUnits.THREADS)
                .register(registry);
    }

    private void monitorThreadPoolExecutor(ThreadPoolTaskExecutor tpte, Iterable<Tag> tags) {
        FunctionCounter.builder(metricPrefix + "executor.completed", tpte, ref -> Safe.get(ref).map(ThreadPoolExecutor::getCompletedTaskCount).orElse(0L))
                .tags(tags)
                .description("The approximate total number of tasks that have completed execution")
                .baseUnit(BaseUnits.TASKS)
                .register(registry);

        Gauge.builder(metricPrefix + "executor.queued", tpte, ref -> Safe.get(ref).map(tpeRef -> tpeRef.getQueue().size()).orElse(0))
                .tags(tags)
                .description("The approximate number of tasks that are queued for execution")
                .baseUnit(BaseUnits.TASKS)
                .register(registry);

        Gauge.builder(metricPrefix + "executor.queue.remaining", tpte, ref -> Safe.get(ref).map(tpeRef -> tpeRef.getQueue().remainingCapacity()).orElse(0))
                .tags(tags)
                .description("The number of additional elements that this queue can ideally accept without blocking")
                .baseUnit(BaseUnits.TASKS)
                .register(registry);
    }

    private static String sanitizePrefix(String metricPrefix) {
        if (StringUtils.isBlank(metricPrefix))
            return "";
        if (!metricPrefix.endsWith("."))
            return metricPrefix + ".";
        return metricPrefix;
    }

    private static class Safe {

        public static Optional<ThreadPoolExecutor> get(ThreadPoolTaskExecutor taskExecutor) {
            try {
                return Optional.of(taskExecutor.getThreadPoolExecutor());
            } catch (IllegalStateException e) {
                // Not initialized until now
                return Optional.empty();
            }
        }
    }

}
