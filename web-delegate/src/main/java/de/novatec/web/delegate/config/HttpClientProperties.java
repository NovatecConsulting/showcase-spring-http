package de.novatec.web.delegate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("http.client")
public class HttpClientProperties {

    private Pool pool = new Pool();

    private Request request = new Request();

    /**
     * If set to true, the default HttpComponentsClientHttpRequestFactory is used. All other configuration is ignored!
     * <p>Default: false
     */
    private boolean useDefaultRequestFactory = false;

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public boolean isUseDefaultRequestFactory() {
        return useDefaultRequestFactory;
    }

    public void setUseDefaultRequestFactory(boolean useDefaultRequestFactory) {
        this.useDefaultRequestFactory = useDefaultRequestFactory;
    }

    public static class Pool {

        /**
         * Maximum limit of connection in total.
         * <p>Default: 20
         */
        private Integer maxConnectionsTotal;

        /**
         * Maximum limit of connection per route (host:port).
         * <p>Default: 2
         */
        private Integer defaultMaxConnectionsPerRoute;

        /**
         *  The total time to live of a persistent connection. A connection will never be re-used beyond its TTL.
         *  <p>One of the purpose of the TTL parameter is ensure a more equal redistribution of persistent connection across a cluster of nodes.
         *  <p>A value of zero or less is interpreted as an infinite TTL.
         *  <p>Default: -1ms
         */
        private Duration timeToLive;

        /**
         * Defines whether connections are re-used or not.
         * <p>If set to false, pooling is actually disabled.
         * <p>Default: true
         */
        private Boolean keepAlive;

        public Integer getDefaultMaxConnectionsPerRoute() {
            return defaultMaxConnectionsPerRoute;
        }

        public void setDefaultMaxConnectionsPerRoute(Integer defaultMaxConnectionsPerRoute) {
            this.defaultMaxConnectionsPerRoute = defaultMaxConnectionsPerRoute;
        }

        public Integer getMaxConnectionsTotal() {
            return maxConnectionsTotal;
        }

        public void setMaxConnectionsTotal(Integer maxConnectionsTotal) {
            this.maxConnectionsTotal = maxConnectionsTotal;
        }

        public Duration getTimeToLive() {
            return timeToLive;
        }

        public void setTimeToLive(Duration timeToLive) {
            this.timeToLive = timeToLive;
        }

        public Boolean isKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(Boolean keepAlive) {
            this.keepAlive = keepAlive;
        }
    }

    public static class Request {

        /**
         * The timeout used when requesting a connection from the connection manager.
         * <p>A timeout value of zero is interpreted as an infinite timeout. A value of -1ms is interpreted as undefined.
         * <p>Default: -1ms
         */
        private Duration connectionRequestTimeout;

        /**
         * The timeout until a connection is established.
         * <p>A timeout value of zero is interpreted as an infinite timeout. A value of -1ms is interpreted as undefined.
         * <p>Default: -1ms
         */
        private Duration connectTimeout;

        /**
         * The socket timeout, which is the timeout for waiting for data or, put differently, a maximum period inactivity between two consecutive data packets).
         * <p>A timeout value of zero is interpreted as an infinite timeout. A value of -1ms is interpreted as undefined.
         * <p>Default: -1ms
         */
        private Duration socketTimeout;

        public Duration getConnectionRequestTimeout() {
            return connectionRequestTimeout;
        }

        public void setConnectionRequestTimeout(Duration connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
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
    }

}
