package net.uweeisele.http11.webflux.delegate.config;

import io.micrometer.core.instrument.MeterRegistry;
import net.uweeisele.http11.webflux.delegate.support.metrics.EventLoopGroupMetricsBinder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.util.function.Supplier;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NettyLoopResourcesProperties.class)
public class NettyLoopResourcesConfiguration {

    /**
     * {@link ReactorResourceFactory} is shared between server and clients and
     * provides {@link reactor.netty.resources.LoopResources} and {@link reactor.netty.resources.ConnectionProvider}.
     * {@link reactor.netty.resources.ConnectionProvider} is only used by clients.
     * <p>This {@link ReactorResourceFactory} is only created to enable custom configuration.
     * If the defaults are fine, this bean could be omitted.
     * <p>Default configuration:
     * <ul>
     *     <li>LoopResources (LoopResources.create("reactor-http")):</li>
     *     <ul>
     *         <li>Worker count: available processor count (but with a minimum value of 4)</li>
     *         <li>No selector threads (worker threads are used)</li>
     *         <li>daemon threads: true</li>
     *         <li>Shutdown quiet period: 2s</li>
     *         <li>Shutdown timeout: 15s</li>
     *     </ul>
     *     <li>ConnectionProvider (ConnectionProvider.create("http", 500)):</li>
     *     <ul>
     *         <li>Leasing strategy: fifo</li>
     *         <li>Max connections (per address): 500</li>
     *         <li>Max idle time of a channel: infinite</li>
     *         <li>Max life time of a channel: infinite</li>
     *         <li>Metrics disabled</li>
     *         <li>Pending acquire max count: 1000</li>
     *         <li>Pending acquire timeout: 45s</li>
     *         <li>Background eviction is disabled</li>
     *     </ul>
     * </ul>
     */
    @Bean
    public ReactorResourceFactory reactorResourceFactory(
            ObjectProvider<Supplier<ConnectionProvider>> connectionProviderSupplier,
            ObjectProvider<Supplier<LoopResources>> loopResourcesSupplier,
            NettyLoopResourcesProperties properties) {
        final ReactorResourceFactory reactorResourceFactory = new ReactorResourceFactory();
        // the usage of default global resources must be disabled in order to use custom configured ones
        reactorResourceFactory.setUseGlobalResources(false);
        // if supplied custom LoopResources and ConnectionProvider are added,
        // otherwise default instances are automatically created by this resource factory
        connectionProviderSupplier.ifUnique(reactorResourceFactory::setConnectionProviderSupplier);
        loopResourcesSupplier.ifUnique(reactorResourceFactory::setLoopResourcesSupplier);
        // shutdown quiet period and timeout are required for the LoopResources to enable graceful shutdown
        reactorResourceFactory.setShutdownQuietPeriod(properties.getShutdownQuietPeriod());
        reactorResourceFactory.setShutdownTimeout(properties.getShutdownTimeout());
        return reactorResourceFactory;
    }

    @Bean
    public Supplier<LoopResources> loopResourcesSupplier(NettyLoopResourcesProperties properties, MeterRegistry meterRegistry) {
        return new LoopResourcesBuilder(properties)
                .withMeterRegistry(meterRegistry);
    }

    public static class LoopResourcesBuilder implements Supplier<LoopResources> {

        private final NettyLoopResourcesProperties properties;

        private EventLoopGroupMetricsBinder metricsBinder;

        public LoopResourcesBuilder(NettyLoopResourcesProperties properties) {
            this.properties = properties;
        }

        public LoopResourcesBuilder withMeterRegistry(MeterRegistry meterRegistry) {
            this.metricsBinder = new EventLoopGroupMetricsBinder(meterRegistry);
            return this;
        }

        @Override
        public LoopResources get() {
            if (isUseDedicatedSelectEventLoop()) {
                return buildWithDedicatedSelectEventLoop();
            }
            return buildWithSharedSelectAndServerEventLoop();
        }

        private boolean isUseDedicatedSelectEventLoop() {
            return properties.getSelectCount() >= 1;
        }

        private LoopResources buildWithDedicatedSelectEventLoop() {
            final LoopResources loopResources = LoopResources.create(
                    properties.getPrefix(),
                    properties.getSelectCount(),
                    properties.getWorkerCount(),
                    properties.isDaemon());
            if (metricsBinder != null && properties.isMetrics()) {
                // client uses always same EventLoopGroup as server
                metricsBinder.monitor(loopResources.onServer(LoopResources.DEFAULT_NATIVE));
                metricsBinder.monitor(loopResources.onServerSelect(LoopResources.DEFAULT_NATIVE));
            }
            return loopResources;
        }

        private LoopResources buildWithSharedSelectAndServerEventLoop() {
            final LoopResources loopResources = LoopResources.create(
                    properties.getPrefix(),
                    properties.getWorkerCount(),
                    properties.isDaemon());
            if (metricsBinder != null && properties.isMetrics()) {
                // client uses always same EventLoopGroup as server
                metricsBinder.monitor(loopResources.onServer(LoopResources.DEFAULT_NATIVE));
            }
            return loopResources;
        }

    }

}
