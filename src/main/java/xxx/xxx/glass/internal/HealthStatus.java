package xxx.xxx.glass.internal;

import xxx.xxx.glass.Glass;

import java.util.Queue;

/**
 * Not fully implemented, used to monitor the status of the plugin.
 */

@Deprecated
public class HealthStatus {

    private final ThreadGroup threadGroup;
    private final Queue<?> queue;

    public HealthStatus(final ThreadGroup threadGroup, final Queue<?> queue) {

        this.threadGroup = threadGroup;
        this.queue = queue;

    }

    /**
     * Used to calculate a current health level based on usage.
     *
     * @return The calculated health level.
     */

    public double calculateHealthLevel() {

        double healthLevel = 1.0;
        healthLevel -= Math.max((threadGroup.activeCount() - Glass.QUEUE_THREAD_COUNT) * 0.03, 0);
        healthLevel -= Math.max(queue.size() * 0.008, 0);

        return healthLevel;

    }

    /**
     * Used to return the main thread group.
     *
     * @return The main thread group.
     */

    public ThreadGroup getThreadGroup() {

        return threadGroup;

    }

    /**
     * Used to return the current queue size.
     *
     * @return The current queue size.
     */

    public int getQueueSize() {

        return queue.size();

    }

}
