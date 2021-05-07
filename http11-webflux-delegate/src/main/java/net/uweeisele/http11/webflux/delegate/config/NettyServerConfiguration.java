package net.uweeisele.http11.webflux.delegate.config;

import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class NettyServerConfiguration {

    @Bean
    public NettyServerCustomizer nettyServerCustomizer(){
        return httpServer -> httpServer.metrics(true, uri -> uri);
    }
}
