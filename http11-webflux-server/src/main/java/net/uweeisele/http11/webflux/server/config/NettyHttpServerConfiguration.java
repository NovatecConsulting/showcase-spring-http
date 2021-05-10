package net.uweeisele.http11.webflux.server.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.LoopResources;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * The {@link org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory} is used for configuring Netty HTTP Server:
 * <ul>
 *   <li>{@link org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory} which provides {@link HttpServer} is provided by ReactiveWebServerFactoryConfiguration.EmbeddedNetty class.</li>
 *   <li>{@link ReactorResourceFactory} which provides {@link LoopResources} is added to {@link org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory} by ReactiveWebServerFactoryConfiguration.EmbeddedNetty.</li>
 * </ul>
 *
 * The following customizations for {@link org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory} are possible:
 * <ul>
 *   <li>
 *       {@link LoopResources} can be configured by providing a custom {@link ReactorResourceFactory}.
 *       {@link ReactorResourceFactory} by default is shared between {@link ReactorClientHttpConnector} for clients and {@link org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory} for server.
 *       In order to have separate {@link LoopResources} for client and server, a custom {@link ReactorClientHttpConnector} for clients and {@link org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory} for server must be provided.
 *       This includes adding of {@link ReactorResourceFactory} and applying of {@link NettyServerCustomizer} to the manually created {@link org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory}.
 *   </li>
 *   <li>{@link HttpServer} can be configured by {@link NettyServerCustomizer} which are applied to{@link org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory} by ReactiveWebServerFactoryConfiguration.EmbeddedNetty.</li>
 * </ul>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NettyHttpServerProperties.class)
public class NettyHttpServerConfiguration {

    @Bean
    public NettyServerCustomizer commonNettyHttpServerCustomizer(NettyHttpServerProperties properties){
        return httpServer -> {
            HttpServer transformedHttpServer = httpServer
                    .metrics(properties.isMetrics(), uri -> uri)
                    .protocol(properties.getProtocols());
            if (properties.getReadTimeout() != null) {
                transformedHttpServer = transformedHttpServer
                        .doOnConnection(conn -> conn
                            .addHandlerFirst(new ReadTimeoutHandler(properties.getReadTimeout().toMillis(), MILLISECONDS)));
            }
            return transformedHttpServer;
        };
    }

    @Bean
    public NettyServerCustomizer http2NettyHttpServerCustomizer(NettyHttpServerProperties properties){
        return httpServer -> {
            if (properties.getHttp2() != null){
                return httpServer.http2Settings(builder -> {
                    final PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
                    map.from(properties.getHttp2().getMaxConcurrentStreams()).to(builder::maxConcurrentStreams);
                });
            }
            return httpServer;
        };
    }

}
