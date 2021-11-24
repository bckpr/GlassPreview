package xxx.xxx.glass.analyzing;

import xxx.xxx.glass.data.entry.Entry;

/**
 * Contract for EntrySubscriber implementations.
 */

public interface EntrySubscriber {

    void onNewEntry(final Entry entry);

}
