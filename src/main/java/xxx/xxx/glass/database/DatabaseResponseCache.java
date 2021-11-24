package xxx.xxx.glass.database;

import xxx.xxx.glass.common.SyncLazyCache;
import xxx.xxx.glass.common.TimeSpan;

/**
 * Used to cache DatabaseResponse instances for a certain amount of time.
 */

public class DatabaseResponseCache extends SyncLazyCache<DatabaseResponse> {

    public DatabaseResponseCache(final TimeSpan timeSpan) {

        super(timeSpan);

    }

}
