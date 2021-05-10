package net.uweeisele.http11.webflux.delegate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import reactor.netty.resources.LoopResources;

import java.time.Duration;

@ConfigurationProperties("netty.event-loop")
public class NettyLoopResourcesProperties {

    /**
     * The event loop thread name prefix.
     */
    private String prefix = "webflux-http";

    /**
     * The server selector thread count.
     * <p>By default this is set to {@link LoopResources#DEFAULT_IO_SELECT_COUNT}
     * which is -1 (no selector thread).
     */
    private int selectCount = LoopResources.DEFAULT_IO_SELECT_COUNT;

    /**
     * The worker thread count.
     * <p>By default this is set to {@link LoopResources#DEFAULT_IO_WORKER_COUNT}
     * which is the number of available processors (but with a minimum value of 4).
     */
    private int workerCount = LoopResources.DEFAULT_IO_WORKER_COUNT;

    /**
     * Should the thread be released on jvm shutdown. Default is true.
     */
    private boolean daemon = true;

    /**
     * The amount of time we'll wait before shutting down resources.
     * If a task is submitted during the {@code shutdownQuietPeriod}, it is guaranteed
     * to be accepted and the {@code shutdownQuietPeriod} will start over.
     * <p>By default, this is set to
     * {@link LoopResources#DEFAULT_SHUTDOWN_QUIET_PERIOD} which is 2 seconds.
     */
    private Duration shutdownQuietPeriod = Duration.ofSeconds(LoopResources.DEFAULT_SHUTDOWN_QUIET_PERIOD);

    /**
     * The maximum amount of time to wait until the disposal of the
     * underlying resources regardless if a task was submitted during the
     * {@code shutdownQuietPeriod}.
     * <p>By default, this is set to
     * {@link LoopResources#DEFAULT_SHUTDOWN_TIMEOUT} which is 15 seconds.
     */
    private Duration shutdownTimeout = Duration.ofSeconds(LoopResources.DEFAULT_SHUTDOWN_TIMEOUT);

    /**
     * Should micrometer metrics for {@link io.netty.util.concurrent.EventExecutor} are enabled.
     * <p><strong>Be aware that this may be expensive as it depends on the internal implementation of the
     * {@link io.netty.util.concurrent.EventExecutor}. So use it with care!</strong>
     * <p>Default is false.
     */
    private boolean metrics = false;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public Duration getShutdownQuietPeriod() {
        return shutdownQuietPeriod;
    }

    public void setShutdownQuietPeriod(Duration shutdownQuietPeriod) {
        this.shutdownQuietPeriod = shutdownQuietPeriod;
    }

    public Duration getShutdownTimeout() {
        return shutdownTimeout;
    }

    public void setShutdownTimeout(Duration shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }

    public boolean isMetrics() {
        return metrics;
    }

    public void setMetrics(boolean metrics) {
        this.metrics = metrics;
    }

}
