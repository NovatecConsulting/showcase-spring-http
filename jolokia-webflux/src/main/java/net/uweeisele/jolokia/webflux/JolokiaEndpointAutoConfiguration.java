package net.uweeisele.jolokia.webflux;

import org.jolokia.backend.BackendManager;
import org.jolokia.http.HttpRequestHandler;
import org.jolokia.restrictor.Restrictor;
import org.jolokia.restrictor.RestrictorFactory;
import org.jolokia.util.JulLogHandler;
import org.jolokia.util.LogHandler;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;

import static org.jolokia.config.ConfigKey.AGENT_ID;
import static org.jolokia.util.NetworkUtil.getAgentId;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = REACTIVE)
@EnableConfigurationProperties
public class JolokiaEndpointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public JolokiaEndpoint jolokiaEndpoint(
            HttpRequestHandler jolokiaHttpRequestHandler,
            JolokiaSchedulerProvider jolokiaScheduler) {
        return new JolokiaEndpoint(jolokiaHttpRequestHandler, jolokiaScheduler.get());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(JolokiaEndpoint.class)
    public HttpRequestHandler jolokiaHttpRequestHandler() {
        final org.jolokia.config.Configuration config = new org.jolokia.config.Configuration(
                AGENT_ID, getAgentId(hashCode(),"reactive"));
        final LogHandler logHandler = new JulLogHandler();
        final Restrictor restrictor = RestrictorFactory.createRestrictor(config, logHandler);
        final BackendManager backendManager = new BackendManager(config, logHandler, restrictor);
        return new HttpRequestHandler(config, backendManager, logHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(JolokiaEndpoint.class)
    public JolokiaSchedulerProvider jolokiaScheduler(JolokiaProperties jolokiaProperties) {
        return () -> Schedulers.newBoundedElastic(
                jolokiaProperties.getScheduler().getThreadCapacity(),
                jolokiaProperties.getScheduler().getQueuedTaskCapacity(),
                jolokiaProperties.getScheduler().getName());
    }

    @Bean
    @ConditionalOnBean(JolokiaEndpoint.class)
    @ConfigurationProperties("jolokia")
    public JolokiaProperties jolokiaProperties() {
        return new JolokiaProperties();
    }
}
