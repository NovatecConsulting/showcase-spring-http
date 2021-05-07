package net.uweeisele.jolokia.webflux;

import reactor.core.scheduler.Scheduler;

import java.util.function.Supplier;

public interface JolokiaSchedulerProvider extends Supplier<Scheduler> {
}
