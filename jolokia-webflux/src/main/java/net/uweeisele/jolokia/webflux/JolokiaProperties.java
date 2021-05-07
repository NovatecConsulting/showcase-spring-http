package net.uweeisele.jolokia.webflux;

import static java.lang.Math.max;
import static java.lang.Runtime.getRuntime;

public class JolokiaProperties {

    private Scheduler scheduler = new Scheduler();

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public static class Scheduler {

        private int threadCapacity = max(1, getRuntime().availableProcessors() / 2);
        private int queuedTaskCapacity = Integer.MAX_VALUE;
        private String name = "jolokiaScheduler";

        public int getThreadCapacity() {
            return threadCapacity;
        }

        public void setThreadCapacity(int threadCapacity) {
            this.threadCapacity = threadCapacity;
        }

        public int getQueuedTaskCapacity() {
            return queuedTaskCapacity;
        }

        public void setQueuedTaskCapacity(int queuedTaskCapacity) {
            this.queuedTaskCapacity = queuedTaskCapacity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
