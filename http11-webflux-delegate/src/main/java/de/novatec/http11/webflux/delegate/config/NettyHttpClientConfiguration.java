package de.novatec.http11.webflux.delegate.config;

import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactorNettyHttpClientMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.util.function.Supplier;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static io.netty.channel.ChannelOption.SO_TIMEOUT;
import static reactor.netty.resources.ConnectionProvider.LEASING_STRATEGY_FIFO;
import static reactor.netty.resources.ConnectionProvider.LEASING_STRATEGY_LIFO;

/**
 * {@link WebClient} can be created by {@link WebClient.Builder} which is provided and configured by the following configuration classes:
 * <ul>
 *   <li>{@link WebClient.Builder} is provided by {@link org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration}.</li>
 *   <li>{@link org.springframework.http.client.reactive.ClientHttpConnector} is added to {@link WebClient.Builder} by {@link org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration} via {@link WebClientCustomizer}.</li>
 *   <li>{@link ReactorClientHttpConnector} which provides the {@link HttpClient} is provided by ClientHttpConnectorConfiguration.ReactorNetty.</li>
 *   <li>{@link org.springframework.http.client.reactive.ReactorResourceFactory} which provides {@link ConnectionProvider} and {@link LoopResources} is added to {@link ReactorClientHttpConnector} by ClientHttpConnectorConfiguration.ReactorNetty.</li>
 * </ul>
 *
 * The following customizations for {@link WebClient.Builder} are possible:
 * <ul>
 *   <li>
 *       {@link ConnectionProvider} and {@link LoopResources} can be configured by providing a custom {@link org.springframework.http.client.reactive.ReactorResourceFactory}.
 *       {@link org.springframework.http.client.reactive.ReactorResourceFactory} by default is shared between {@link ReactorClientHttpConnector} for clients and {@link org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory} for server.
 *       In order to have separate {@link LoopResources} for client and server, a custom {@link ReactorClientHttpConnector} for clients and {@link org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory} for server must be provided.
 *       This includes adding of {@link org.springframework.http.client.reactive.ReactorResourceFactory} and applying of {@link ReactorNettyHttpClientMapper} to the manually created {@link ReactorClientHttpConnector}.
 *   </li>
 *   <li>{@link HttpClient} can be configured by {@link ReactorNettyHttpClientMapper} which are applied to {@link ReactorClientHttpConnector} by ClientHttpConnectorConfiguration.ReactorNetty.</li>
 *   <li>{@link WebClient.Builder} can be configured by {@link WebClientCustomizer} which are applied to {@link WebClient.Builder} by {@link org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration}.</li>
 * </ul>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NettyHttpClientProperties.class)
public class NettyHttpClientConfiguration {

    /**
     * {@link ConnectionProvider} is added to {@link org.springframework.http.client.reactive.ReactorResourceFactory} by
     * {@link NettyLoopResourcesConfiguration}.
     */
    @Bean
    public Supplier<ConnectionProvider> connectionProviderSupplier(NettyHttpClientProperties clientProperties) {
        return () -> {
            final NettyHttpClientProperties.Connection connectProperties = clientProperties.getConnection();
            final ConnectionProvider.Builder builder = ConnectionProvider
                    .builder(connectProperties.getProviderName())
                    .metrics(connectProperties.isMetrics())
                    .maxConnections(connectProperties.getMaxConnections());
            final PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(connectProperties::getPendingAcquireMaxCount).to(builder::pendingAcquireMaxCount);
            map.from(connectProperties::getPendingAcquireTimeout).to(builder::pendingAcquireTimeout);
            map.from(connectProperties::getMaxIdleTime).to(builder::maxIdleTime);
            map.from(connectProperties::getMaxLifeTime).to(builder::maxLifeTime);
            map.from(connectProperties::getLeasingStrategy)
                    .whenEqualTo(LEASING_STRATEGY_FIFO)
                    .toCall(builder::fifo);
            map.from(connectProperties::getLeasingStrategy)
                    .whenEqualTo(LEASING_STRATEGY_LIFO)
                    .toCall(builder::lifo);
            return builder.build();
        };
    }

    @Bean
    public ReactorNettyHttpClientMapper nettyHttpClientCustomizer(NettyHttpClientProperties properties) {
        return httpClient -> {
            HttpClient transformedHttpClient = httpClient
                    .metrics(properties.isMetrics(), uri -> uri)
                    .protocol(properties.getProtocols());
            if (properties.getConnectTimeout() != null) {
                transformedHttpClient = transformedHttpClient.option(CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout().toMillisPart());
            }
            if (properties.getSocketTimeout() != null) {
                transformedHttpClient = transformedHttpClient.option(SO_TIMEOUT, properties.getSocketTimeout().toMillisPart());
            }
            if (properties.getResponseTimeout() != null) {
                transformedHttpClient = transformedHttpClient.responseTimeout(properties.getResponseTimeout());
            }
            return transformedHttpClient;
        };
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }
}
