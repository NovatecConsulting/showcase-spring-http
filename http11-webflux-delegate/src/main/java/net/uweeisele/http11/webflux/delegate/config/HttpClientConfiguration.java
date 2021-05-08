package net.uweeisele.http11.webflux.delegate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientConfiguration {

    private final HttpClientProperties properties;

    @Autowired
    public HttpClientConfiguration(HttpClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create().metrics(true, uri -> uri);
    }

    @Bean
    public WebClientCustomizer httpClientCustomizer(HttpClient httpClient) {
        return (builder) -> builder.clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }
}
