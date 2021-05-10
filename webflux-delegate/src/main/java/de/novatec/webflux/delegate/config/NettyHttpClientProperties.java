package de.novatec.webflux.delegate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import reactor.netty.http.HttpProtocol;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

@ConfigurationProperties("netty.http-client")
public class NettyHttpClientProperties {

    /** Whether to enable metrics to be collected and registered in Micrometer's registry. */
    private boolean metrics = true;

    /**
     * The HTTP protocols to support.
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
     * The timeout until a connection is established.
     * <p>Defaults to no connect timeout.
     */
    private Duration connectTimeout;

    /**
     * The socket timeout, which is the timeout for waiting for data or, put differently, a maximum period inactivity between two consecutive data packets).
     * <p>Defaults to no socket timeout.
     */
    private Duration socketTimeout;

    /**
     * The response timeout duration. This is time that takes to receive a response after sending a request.
     * <p>Defaults to no response timeout.
     */
    private Duration responseTimeout;

    private Connection connection = new Connection();

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

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Duration socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Duration getResponseTimeout() {
        return responseTimeout;
    }

    public void setResponseTimeout(Duration responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public static class Connection {

        /**
         * The name of the connection provider.
         * <p>Used for logs and metrics to distinguish between different providers.
         */
        private String providerName = "webflux";

        /** Whether to enable metrics to be collected and registered in Micrometer's registry. */
        private boolean metrics = true;

        /**
         * The maximum connections per connection pool (i.e. per remote address).
         * <p>By default, a “fixed” connection pool with 500 as the maximum number per remote address is used.
         */
        private int maxConnections = 500;

        /**
         * The maximum number of registered requests for acquire to keep in a pending queue.
         * <p>This option is ignored if HTTP/2 is used, in which case there is no upper limit.
         * <p>When invoked with -1 the pending queue will not have upper limit.
         * <p>Defaults to {@code 2 * max connections}.
         */
        private Integer pendingAcquireMaxCount;

        /**
         * The maximum time after which a pending acquire must complete or the
         * {@link java.util.concurrent.TimeoutException} will be thrown.
         * <p>Default to {@link ConnectionProvider#DEFAULT_POOL_ACQUIRE_TIMEOUT} which is 45s.
         */
        private Duration pendingAcquireTimeout;

        /**
         * Define strategy which is used for the next acquire operation if there are idle connections (i.e. pool is under-utilized).
         * <p>Valid options are:
         * <ul>
         *     <li><b>{@link LeasingStrategy#LIFO}</b>: Configure the pool so that if there are idle connections (i.e. pool is under-utilized),
         *          the next acquire operation will get the <b>Most Recently Used</b> connection
         *          (MRU, i.e. the connection that was released last among the current idle connections).
         *     </li>
         *     <li>
         *         <b>{@link LeasingStrategy#FIFO}</b>: Configure the pool so that if there are idle connections (i.e. pool is under-utilized),
         *          the next acquire operation will get the <b>Least Recently Used</b> connection
         *          (LRU, i.e. the connection that was released first among the current idle connections).
         *     </li>
         * </ul>
         * <p>Default is {@link LeasingStrategy#FIFO}.
         */
        private LeasingStrategy leasingStrategy;

        /**
         * The duration after which the channel will be closed when idle.
         * <p>Defaults to no max idle time.
         */
        private Duration maxIdleTime;

        /**
         * The duration after which the channel will be closed.
         * <p>Defaults to no max life time.
         */
        private Duration maxLifeTime;

        public String getProviderName() {
            return providerName;
        }

        public void setProviderName(String providerName) {
            this.providerName = providerName;
        }

        public boolean isMetrics() {
            return metrics;
        }

        public void setMetrics(boolean metrics) {
            this.metrics = metrics;
        }

        public int getMaxConnections() {
            return maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        public Integer getPendingAcquireMaxCount() {
            return pendingAcquireMaxCount;
        }

        public void setPendingAcquireMaxCount(Integer pendingAcquireMaxCount) {
            this.pendingAcquireMaxCount = pendingAcquireMaxCount;
        }

        public Duration getPendingAcquireTimeout() {
            return pendingAcquireTimeout;
        }

        public void setPendingAcquireTimeout(Duration pendingAcquireTimeout) {
            this.pendingAcquireTimeout = pendingAcquireTimeout;
        }

        public LeasingStrategy getLeasingStrategy() {
            return leasingStrategy;
        }

        public void setLeasingStrategy(LeasingStrategy leasingStrategy) {
            this.leasingStrategy = leasingStrategy;
        }

        public Duration getMaxIdleTime() {
            return maxIdleTime;
        }

        public void setMaxIdleTime(Duration maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
        }

        public Duration getMaxLifeTime() {
            return maxLifeTime;
        }

        public void setMaxLifeTime(Duration maxLifeTime) {
            this.maxLifeTime = maxLifeTime;
        }
    }

    public enum LeasingStrategy {

        /**
         * Configure the pool so that if there are idle connections (i.e. pool is under-utilized),
         * the next acquire operation will get the <b>Most Recently Used</b> connection
         * (MRU, i.e. the connection that was released last among the current idle connections).
         */
        LIFO,

        /**
         * Configure the pool so that if there are idle connections (i.e. pool is under-utilized),
         * the next acquire operation will get the <b>Least Recently Used</b> connection
         * (LRU, i.e. the connection that was released first among the current idle connections).
         */
        FIFO
    }

}
