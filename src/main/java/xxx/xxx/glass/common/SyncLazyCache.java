package xxx.xxx.glass.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple, thread safe and lazy cache implementation.
 *
 * @param <T> Type of the stored objects
 */

public class SyncLazyCache<T> {

    private final Object lock = new Object();
    private final long lifetime;
    private final Map<String, TimedEntry<T>> entries = new HashMap<>();

    /**
     * Simple, thread safe and lazy cache implementation.
     *
     * @param timeSpan Duration after an entry should expire.
     */

    public SyncLazyCache(final TimeSpan timeSpan) {

        this.lifetime = timeSpan.getMillis();

    }

    /**
     * Updates the cache by removing expired entries.
     */

    private void update() {

        entries.entrySet().removeIf(entry -> System.currentTimeMillis() >= entry.getValue().getExpirationTimestamp());

    }

    /**
     * Adds an entry to the internal map.
     *
     * @param identifier The entry identifier.
     * @param value      The entry value.
     */

    public void add(final String identifier, final T value) {

        synchronized (lock) {
            entries.put(identifier, new TimedEntry<>(System.currentTimeMillis() + lifetime, value));
        }

    }

    /**
     * Adds an entry to the internal map with a custom lifetime time span.
     *
     * @param identifier The entry identifier.
     * @param value      The entry value.
     */

    public void add(final String identifier, final T value, final TimeSpan timeSpan) {

        synchronized (lock) {
            entries.put(identifier, new TimedEntry<>(System.currentTimeMillis() + timeSpan.getMillis(), value));
        }

    }

    /**
     * Removes an entry from the internal map.
     *
     * @param identifier The entry identifier.
     */

    public void remove(final String identifier) {

        synchronized (lock) {
            entries.remove(identifier);
        }

    }

    /**
     * Checks if the internal map contains the provided identifier and
     * if the entry (if present) linked to the identifier expired.
     *
     * @param identifier The entry identifier.
     * @return <code>true</code> if the identifier was found.
     */

    public boolean containsIdentifier(final String identifier) {

        synchronized (lock) {
            update();
            return entries.containsKey(identifier);
        }

    }

    /**
     * Gets an entries value if the provided identifier is present and
     * if the entry hasn't expired yet.
     *
     * @param identifier The entry identifier.
     * @return <code>true</code> if the identifier was found.
     */

    public T getByIdentifier(final String identifier) {

        synchronized (lock) {
            update();
            return entries.get(identifier).getValue();
        }

    }

    /**
     * Returns a new list based on all currently non expired entries.
     *
     * @return A list of all active entries.
     */

    public List<T> getActiveEntries() {

        synchronized (lock) {
            update();
            final List<T> activeEntries = new ArrayList<>(entries.size());
            for (final TimedEntry<T> value : entries.values())
                activeEntries.add(value.getValue());
            return activeEntries;
        }

    }

    private static class TimedEntry<T> {

        private final long expirationTimestamp;
        private final T value;

        TimedEntry(final long expirationTimestamp, final T value) {

            this.expirationTimestamp = expirationTimestamp;
            this.value = value;

        }

        long getExpirationTimestamp() {

            return expirationTimestamp;

        }

        T getValue() {

            return value;

        }

    }

}
