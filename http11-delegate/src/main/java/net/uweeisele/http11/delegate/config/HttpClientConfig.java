package net.uweeisele.http11.delegate.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        //connectionManager.setMaxTotal(20);
        //connectionManager.setDefaultMaxPerRoute(4);
        return connectionManager;
    }

    @Bean
    public RequestConfig requestConfig() {
        RequestConfig requestConfig = RequestConfig.custom()
                //.setConnectionRequestTimeout(2000)
                //.setConnectTimeout(2000)
                //.setSocketTimeout(2000)
                .build();
        return requestConfig;
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager, RequestConfig requestConfig) {
        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
        return httpClient;
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, HttpComponentsClientHttpRequestFactory httpRequestFactory) {
        // Usage of RestTemplateBuilder instruments RestTemplate with metrics (http_client_requests)
        RestTemplate restTemplate = builder
                .requestFactory(() -> httpRequestFactory)
                .build();
        return restTemplate;
    }
}
