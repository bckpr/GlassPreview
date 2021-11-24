package xxx.xxx.glass.database;

import xxx.xxx.glass.data.entry.Entry;

import java.util.UUID;

/**
 * DatabaseCommunicator implementation contract.
 */

public interface DatabaseCommunicator {

    void insertEntry(final Entry entry);

    DatabaseResponse findEntryByUniqueId(final String databaseLocation, final UUID uuid);

    DatabaseResponse findEntryByUniqueIdEverywhere(final UUID uuid);

    DatabaseResponse findEntriesByQuery(final String databaseLocation, final String input);

    DatabaseResponse findEntriesByQueryEverywhere(final String input);

    void close();

}
