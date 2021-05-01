package net.uweeisele.http11.client.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.httpcomponents.PoolingHttpClientConnectionManagerMetricsBinder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HttpClientMetricsConfig {

    private MeterRegistry meterRegistry;
    private PoolingHttpClientConnectionManager connectionManager;

    @Autowired
    public HttpClientMetricsConfig(MeterRegistry meterRegistry, PoolingHttpClientConnectionManager connectionManager) {
        this.meterRegistry = meterRegistry;
        this.connectionManager = connectionManager;
    }

    @PostConstruct
    public void setupHttpClientMetrics() {
        new PoolingHttpClientConnectionManagerMetricsBinder(connectionManager, "pool").bindTo(meterRegistry);
    }
}
