package de.novatec.webflux.delegate.config;

import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import reactor.netty.http.HttpProtocol;

import java.time.Duration;

@ConfigurationProperties("netty.http-server")
public class NettyHttpServerProperties {

    /** Whether to enable metrics to be collected and registered in Micrometer's registry. */
    private boolean metrics = true;

    /**
     * The HTTP protocols to support.
     * <p>Overwrites derived protocols based on <i>server.http2.enabled</i> configuration,
     * which only enables http2 (in addition to http1) if ssl is enabled.
     * <p>Supported protocols are:
     * <ul>
     *     <li><b>{@link HttpProtocol#HTTP11}</b>: The default supported HTTP protocol.</li>
     *     <li><b>{@link HttpProtocol#H2C}</b>: HTTP/2.0 support with clear-text. If used along with HTTP/1.1 protocol, will support H2C "upgrade".</li>
     *     <li><b>{@link HttpProtocol#H2}</b>: HTTP/2.0 support with TLS. Not supported by this demo app at the moment.</li>
     * </ul>
     * <p>Default is {@link HttpProtocol#HTTP11}.
     */
    private HttpProtocol[] protocols = new HttpProtocol[] {HttpProtocol.HTTP11};

    /**
     * When no data was read within a this period of time a {@link ReadTimeoutException} is thrown.
     * <p>Defaults to no read timeout.
     */
    private Duration readTimeout;

    /**
     * Optional configuration which is only applied if {@link #protocols} contains
     * {@link HttpProtocol#H2C} or {@link HttpProtocol#H2}.
     */
    private Http2 http2;

    public boolean isMetrics() {
        return metrics;
    }

    public void setMetrics(boolean metrics) {
        this.metrics = metrics;
    }

    public HttpProtocol[] getProtocols() {
        return protocols;
    }

    public void setProtocols(HttpProtocol[] protocols) {
        this.protocols = protocols;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Http2 getHttp2() {
        return http2;
    }

    public void setHttp2(Http2 http2) {
        this.http2 = http2;
    }

    public static class Http2 {

        /**
         * The maximum number of concurrent streams.
         * <p>Defaults to unlimited.
         */
        private Integer maxConcurrentStreams;

        public Integer getMaxConcurrentStreams() {
            return maxConcurrentStreams;
        }

        public void setMaxConcurrentStreams(Integer maxConcurrentStreams) {
            this.maxConcurrentStreams = maxConcurrentStreams;
        }
    }
}
