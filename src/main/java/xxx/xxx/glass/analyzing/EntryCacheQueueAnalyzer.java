package xxx.xxx.glass.analyzing;

import xxx.xxx.glass.Glass;
import xxx.xxx.glass.common.CacheQueue;
import xxx.xxx.glass.common.FluentBuilder;
import xxx.xxx.glass.common.ServiceProvider;
import xxx.xxx.glass.common.SyncLazyCache;
import xxx.xxx.glass.data.EntryCacheQueue;
import xxx.xxx.glass.data.entry.Entry;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Used to forward new queue items to subscribers to they can analyze them.
 */

public class EntryCacheQueueAnalyzer implements CacheQueue.Listener<Entry> {

    private SyncLazyCache<Entry> cache;
    private JavaPlugin plugin;
    private EntrySubscriber[] subscribers;

    private EntryCacheQueueAnalyzer() {

    }

    public EntryCacheQueueAnalyzer(final ServiceProvider serviceProvider, final EntrySubscriber... subscribers) {

        this.cache = serviceProvider.getService(EntryCacheQueue.class).getCache();
        this.plugin = serviceProvider.getService(Glass.class);
        this.subscribers = subscribers;

    }

    /**
     * Gets called when a new entry gets added to the cache queue. This method
     * ensures that the subscribers accept them on the main thread.
     *
     * @param entry The new entry.
     */

    @Override
    public void onAdd(final Entry entry) {

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (final EntrySubscriber subscriber : subscribers)
                subscriber.onNewEntry(entry);
        });

    }

    /**
     * Used to get the underlying cache.
     *
     * @return The cache.
     */

    public SyncLazyCache<Entry> getCache() {

        return cache;

    }

    /**
     * Used to set the underlying cache.
     *
     * @param cache The cache.
     */

    public void setCache(final SyncLazyCache<Entry> cache) {

        this.cache = cache;

    }

    /**
     * Used to get all registered subscribers.
     *
     * @return The registered subscribers.
     */

    public EntrySubscriber[] getSubscribers() {

        return subscribers;

    }

    /**
     * Used to set the registered subscribers.
     *
     * @param subscribers The registered subscribers.
     */

    public void setSubscribers(final EntrySubscriber... subscribers) {

        this.subscribers = subscribers;

    }

    public static class Builder implements FluentBuilder<EntryCacheQueueAnalyzer> {

        private final EntryCacheQueueAnalyzer entryCacheQueueAnalyzer = new EntryCacheQueueAnalyzer();

        public Builder cache(final SyncLazyCache<Entry> cache) {

            entryCacheQueueAnalyzer.setCache(cache);
            return this;

        }

        public Builder subscribers(final EntrySubscriber... subscribers) {

            entryCacheQueueAnalyzer.setSubscribers(subscribers);
            return this;

        }

        public EntryCacheQueueAnalyzer build() {

            return entryCacheQueueAnalyzer;

        }

    }

}
