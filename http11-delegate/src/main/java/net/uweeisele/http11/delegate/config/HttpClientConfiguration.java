package net.uweeisele.http11.delegate.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.httpcomponents.PoolingHttpClientConnectionManagerMetricsBinder;
import net.uweeisele.metrics.MetricsBinder;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.uweeisele.metrics.MicrometerSupport.toMicrometerTags;
import static net.uweeisele.metrics.ReflectionSupport.doIfValueIsPresent;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientConfiguration {

    private final HttpClientProperties properties;

    @Autowired
    public HttpClientConfiguration(HttpClientProperties properties) {
        this.properties = properties;
    }

    @Lazy
    @Bean
    public MetricsBinder<PoolingHttpClientConnectionManager> poolingHttpClientConnectionManagerMetricsBinder(MeterRegistry meterRegistry, MetricsProperties properties) {
        return (connectionManager, poolName) ->
                new PoolingHttpClientConnectionManagerMetricsBinder(
                        connectionManager,
                        poolName,
                        toMicrometerTags(properties.getTags()))
                        .bindTo(meterRegistry);
    }

    @Lazy
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(
            MetricsBinder<PoolingHttpClientConnectionManager> metricsBinder) {
        final PoolingHttpClientConnectionManager connectionManager = ofNullable(properties.getPool().getTimeToLive())
                .map(ttl -> new PoolingHttpClientConnectionManager(ttl.toMillis(), MILLISECONDS))
                .orElse(new PoolingHttpClientConnectionManager());

        final PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(properties.getPool().getMaxConnectionsTotal()).to(connectionManager::setMaxTotal);
        map.from(properties.getPool().getDefaultMaxConnectionsPerRoute()).to(connectionManager::setDefaultMaxPerRoute);

        metricsBinder.monitor(connectionManager, "global");

        return connectionManager;
    }

    @Lazy
    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        final RequestConfig.Builder configBuilder = RequestConfig.custom();

        final PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(properties.getRequest().getConnectionRequestTimeout())
                .asInt(Duration::toMillis)
                .to(configBuilder::setConnectionRequestTimeout);
        map.from(properties.getRequest().getConnectTimeout())
                .asInt(Duration::toMillis)
                .to(configBuilder::setConnectTimeout);
        map.from(properties.getRequest().getSocketTimeout())
                .asInt(Duration::toMillis)
                .to(configBuilder::setSocketTimeout);

        return HttpClientBuilder
                .create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(configBuilder.build())
                .setConnectionReuseStrategy(
                        ofNullable(properties.getPool().isKeepAlive())
                                .filter(keepAlive -> !keepAlive)
                                .map(v -> NoConnectionReuseStrategy.INSTANCE)
                                .orElse(null))
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "http.client" ,name = "use-default-request-factory", havingValue = "true")
    public HttpComponentsClientHttpRequestFactory httpRequestFactoryDefault(MetricsBinder<PoolingHttpClientConnectionManager> metricsBinder) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //Instrument with metrics if possible.
        //This is only done be able to monitor default implementation for demonstration purpose.
        //Do not do this in production, set a custom ConnectionManager instead.
        doIfValueIsPresent(factory.getHttpClient(), "connManager", PoolingHttpClientConnectionManager.class, connManager ->
                metricsBinder.monitor(connManager, "global")
        );
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean(HttpComponentsClientHttpRequestFactory.class)
    public HttpComponentsClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }



    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, HttpComponentsClientHttpRequestFactory httpRequestFactory) {
        // Usage of RestTemplateBuilder adds micrometer metrics for RestTemplate
        return builder
                .requestFactory(() -> httpRequestFactory)
                .build();
    }
}
