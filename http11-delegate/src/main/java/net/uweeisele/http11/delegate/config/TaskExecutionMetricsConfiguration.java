package net.uweeisele.http11.delegate.config;

import io.micrometer.core.instrument.MeterRegistry;
import net.uweeisele.metrics.ThreadPoolTaskExecutorMetricsBinder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static net.uweeisele.metrics.MicrometerSupport.toMicrometerTags;

@Configuration(proxyBeanMethods = false)
public class TaskExecutionMetricsConfiguration {

    @Lazy
    @Bean
    public ThreadPoolTaskExecutorMetricsBinder metricsThreadPoolTaskExecutorMetricsBinder(MeterRegistry meterRegistry, MetricsProperties properties) {
        return new ThreadPoolTaskExecutorMetricsBinder(meterRegistry, "", toMicrometerTags(properties.getTags()));
    }

    @Bean
    public BeanPostProcessor metricsThreadPoolTaskExecutorBeanPostProcessor(ThreadPoolTaskExecutorMetricsBinder metricsBinder) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof ThreadPoolTaskExecutor) {
                    metricsBinder.monitor((ThreadPoolTaskExecutor)bean, beanName);
                }
                return bean;
            }
        };
    }

}
