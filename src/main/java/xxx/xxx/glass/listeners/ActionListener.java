package xxx.xxx.glass.listeners;

import xxx.xxx.glass.common.CacheQueue;
import xxx.xxx.glass.data.entry.Entry;

/**
 * Implementations of this class usually create Entry instances that have to be queued.
 */

public abstract class ActionListener {

    private final CacheQueue<Entry> queue;

    public ActionListener(final CacheQueue<Entry> queue) {

        this.queue = queue;

    }

    /**
     * Used to return the underlying queue.
     *
     * @return The queue.
     */

    public CacheQueue<Entry> getQueue() {

        return queue;

    }

}
