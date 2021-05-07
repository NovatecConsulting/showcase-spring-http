package net.uweeisele.http11.webflux.delegate.support.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class TimedRunnable implements Runnable {

    private final MeterRegistry registry;
    private final Timer executionTimer;
    private final Timer idleTimer;
    private final Runnable command;
    private final Timer.Sample idleSample;

    public TimedRunnable(MeterRegistry registry, Timer executionTimer, Timer idleTimer, Runnable command) {
        this.registry = registry;
        this.executionTimer = executionTimer;
        this.idleTimer = idleTimer;
        this.command = command;
        this.idleSample = Timer.start(registry);
    }

    @Override
    public void run() {
        idleSample.stop(idleTimer);
        Timer.Sample executionSample = Timer.start(registry);
        try {
            command.run();
        } finally {
            executionSample.stop(executionTimer);
        }
    }
}