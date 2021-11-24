package xxx.xxx.glass.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Used to combine a cache with a queue and inform listeners about updates.
 *
 * @param <T> The type.
 */

public class CacheQueue<T> {

    private final LinkedBlockingQueue<T> queue;
    private final SyncLazyCache<T> cache;
    private final List<Listener<T>> listeners;

    public CacheQueue(final LinkedBlockingQueue<T> queue, final SyncLazyCache<T> cache) {

        this.queue = queue;
        this.cache = cache;
        this.listeners = new ArrayList<>();

    }

    @SafeVarargs
    public CacheQueue(final LinkedBlockingQueue<T> queue, final SyncLazyCache<T> cache, final Listener<T>... listeners) {

        this.queue = queue;
        this.cache = cache;
        this.listeners = Arrays.asList(listeners);

    }

    /**
     * Used to add an object to the cache and queue and informs all listeners.
     *
     * @param obj The object.
     * @return <code>true</code> after the process has been completed.
     */

    public boolean add(final T obj) {

        cache.add(Long.toString(System.currentTimeMillis()), obj);
        queue.add(obj);

        listeners.forEach(listener -> listener.onAdd(obj));

        return true;

    }

    /**
     * Used to take an object from the underlying queue, blocks if
     * the queue is empty until a new object becomes available.
     *
     * @return The object.
     * @throws InterruptedException Gets thrown if the process gets interrupted.
     */

    public T take() throws InterruptedException {

        return queue.take();

    }

    /**
     * Used to check if the underlying queue is empty.
     *
     * @return <code>true</code> if the queue is empty, <code>false</code> otherwise.
     */

    public boolean isEmpty() {

        return queue.isEmpty();

    }

    /**
     * Used to get the underlying queue.
     *
     * @return The underlying queue.
     */

    public Queue<T> getQueue() {

        return queue;

    }

    /**
     * Used to get the underlying cache.
     *
     * @return The underlying cache.
     */

    public SyncLazyCache<T> getCache() {

        return cache;

    }

    /**
     * Used to get all registered listeners in a list.
     *
     * @return The list of registered listeners.
     */

    public List<Listener<T>> getListeners() {

        return listeners;

    }

    /**
     * Used to register a new listener.
     *
     * @param listener The listener.
     */

    public void registerListener(final Listener<T> listener) {

        listeners.add(listener);

    }

    /**
     * Contract for listener implementations.
     *
     * @param <T> The type that the listener accepts.
     */

    public interface Listener<T> {

        void onAdd(T obj);

    }

}
