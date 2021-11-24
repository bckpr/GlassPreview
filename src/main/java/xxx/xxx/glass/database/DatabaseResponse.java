package xxx.xxx.glass.database;

import xxx.xxx.glass.data.entry.Entry;

import java.util.List;

/**
 * Holds entries returned from database requests.
 */

public class DatabaseResponse {

    public static final DatabaseResponse SUCCEEDED = new DatabaseResponse(Status.SUCCEEDED);
    public static final DatabaseResponse FAILED = new DatabaseResponse(Status.FAILED);

    private final Status status;
    private List<Entry> entries = null;
    private final long creationTimestamp;

    public DatabaseResponse(final Status status) {

        this.status = status;
        this.creationTimestamp = System.currentTimeMillis();

    }

    public DatabaseResponse(final Status status, final List<Entry> entries) {

        this.status = status;
        this.entries = entries;
        this.creationTimestamp = System.currentTimeMillis();

    }

    /**
     * Used to get the status.
     *
     * @return The status.
     */

    public Status getStatus() {

        return status;

    }

    /**
     * Used to check if the instance contains any entries.
     *
     * @return <code>true</code> if there are any entries, <code>false</code> otherwise.
     */

    public boolean hasEntries() {

        return entries != null && !entries.isEmpty();

    }

    /**
     * Used to get a list of all stored entries.
     *
     * @return The list of entries.
     */

    public List<Entry> getEntries() {

        return entries;

    }

    /**
     * Used to get the first entry in the entry list, it
     * might return <code>null</code> if the entry list
     * is empty.
     *
     * @return The first entry.
     */

    public Entry getEntry() {

        return entries.size() > 0 ? entries.get(0) : null;

    }

    /**
     * Used to get the unix creation timestamp of the instance.
     *
     * @return The creation timestamp.
     */

    public long getCreationTimestamp() {

        return creationTimestamp;

    }

    public enum Status {

        SUCCEEDED,
        FAILED

    }

}
