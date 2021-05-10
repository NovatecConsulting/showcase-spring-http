package de.novatec.http11.webflux.delegate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;

@Configuration(proxyBeanMethods = false)
public class ReactorConfiguration {

    @Bean
    public void enableSchedulerMetrics() {
        Schedulers.enableMetrics();
    }
}
