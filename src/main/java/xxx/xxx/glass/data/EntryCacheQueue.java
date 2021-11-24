package xxx.xxx.glass.data;

import xxx.xxx.glass.common.CacheQueue;
import xxx.xxx.glass.common.SyncLazyCache;
import xxx.xxx.glass.data.entry.Entry;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * CacheQueue implementation for entries.
 */

public class EntryCacheQueue extends CacheQueue<Entry> {

    public EntryCacheQueue(final LinkedBlockingQueue<Entry> queue, final SyncLazyCache<Entry> cache) {

        super(queue, cache);

    }

    @SafeVarargs
    public EntryCacheQueue(final LinkedBlockingQueue<Entry> queue, final SyncLazyCache<Entry> cache, final Listener<Entry>... listeners) {

        super(queue, cache, listeners);

    }

}
