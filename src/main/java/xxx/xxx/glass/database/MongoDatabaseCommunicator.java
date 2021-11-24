package xxx.xxx.glass.database;

import xxx.xxx.glass.data.entry.Action;
import xxx.xxx.glass.data.entry.Entry;
import xxx.xxx.glass.data.parser.EntryParserRegister;
import xxx.xxx.glass.utils.MongoUtils;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * DatabaseCommunicator implementation for MongoDB
 */

public class MongoDatabaseCommunicator implements DatabaseCommunicator {

    private MongoClient client;
    private MongoDatabase database;

    private final EntryParserRegister entryParserRegister;
    private final DatabaseResponseCache databaseResponseCache;

    public MongoDatabaseCommunicator(final EntryParserRegister entryParserRegister, final DatabaseResponseCache databaseResponseCache) {

        this.entryParserRegister = entryParserRegister;
        this.databaseResponseCache = databaseResponseCache;

    }

    /**
     * Connects to the mongodb server and initializes the client and
     * database field. This method has to be called before anything.
     *
     * @param host     The mongodb server host.
     * @param port     The port.
     * @param database The database name.
     */

    public void connect(final String host, final int port, final String database) {

        this.client = new MongoClient(host, port);
        this.database = client.getDatabase(database);

    }

    /**
     * Closes the connection to the mongodb server.
     */

    public void close() {

        client.close();

    }

    /**
     * Gets the collection by the specified name or creates it if it
     * doesn't exist yet.
     *
     * @param name The collection name.
     * @return The collection.
     */

    @NotNull
    private MongoCollection<Document> getCollection(final String name) {

        if (!MongoUtils.hasCollection(database, name))
            database.createCollection(name);

        return database.getCollection(name);

    }

    /**
     * Used to insert an entry into its proper collection. This method will
     * automatically resolve the collection name.
     *
     * @param entry The entry to insert.
     */

    public void insertEntry(final Entry entry) {

        final MongoCollection<Document> collection = getCollection(entry.getDatabaseIdentifier());
        final Document document = new Document();
        if (entry.exportData(document)) collection.insertOne(document);

    }

    /**
     * Used to find an entry in a specific database section by its unique id.
     *
     * @param databaseLocation The database location to look in.
     * @param uuid             The unique id of the wanted document.
     * @return A DatabaseResponse containing the potential result(s).
     */

    public DatabaseResponse findEntryByUniqueId(final String databaseLocation, final UUID uuid) {

        final Document query = new Document("uuid", uuid);

        final String cacheIdentifier = String.format("%s-%s-%s", "findEntryByUniqueId", databaseLocation, query);
        if (databaseResponseCache.containsIdentifier(cacheIdentifier))
            return databaseResponseCache.getByIdentifier(cacheIdentifier);

        final Document document = getCollection(databaseLocation).find(query).maxTime(15, TimeUnit.SECONDS).first();
        if (document == null)
            return cacheDatabaseResponse(cacheIdentifier, DatabaseResponse.SUCCEEDED);

        final Action action = Action.parseSafe(document.getString("action"));
        if (action == Action.UNKNOWN)
            return cacheDatabaseResponse(cacheIdentifier, DatabaseResponse.SUCCEEDED);

        final Entry entry = entryParserRegister.get(action).parse(document);
        return cacheDatabaseResponse(cacheIdentifier, new DatabaseResponse(DatabaseResponse.Status.SUCCEEDED, Collections.singletonList(entry)));

    }

    /**
     * Used to find an entry in the entire database structure by its unique id.
     *
     * @param uuid The unique id of the wanted document.
     * @return A DatabaseResponse containing the potential result(s).
     */

    public DatabaseResponse findEntryByUniqueIdEverywhere(final UUID uuid) {

        final Document query = new Document("uuid", uuid);

        final String cacheIdentifier = String.format("%s-%s", "findEntryByUniqueIdEverywhere", query);
        if (databaseResponseCache.containsIdentifier(cacheIdentifier))
            return databaseResponseCache.getByIdentifier(cacheIdentifier);

        for (final String collectionName : database.listCollectionNames()) {
            final Document document = getCollection(collectionName).find(query).maxTime(15, TimeUnit.SECONDS).first();
            if (document == null) continue;
            final Action action = Action.parseSafe(document.getString("action"));
            if (action == Action.UNKNOWN) continue;
            final Entry entry = entryParserRegister.get(action).parse(document);
            return cacheDatabaseResponse(cacheIdentifier, new DatabaseResponse(DatabaseResponse.Status.SUCCEEDED, Collections.singletonList(entry)));
        }

        return cacheDatabaseResponse(cacheIdentifier, DatabaseResponse.FAILED);

    }

    /**
     * Used to find entries in a specific database section by the provided database query.
     *
     * @param databaseLocation The database location to look in.
     * @param input            The database query.
     * @return A DatabaseResponse containing the potential result(s).
     */

    public DatabaseResponse findEntriesByQuery(final String databaseLocation, final String input) {

        final String cacheIdentifier = String.format("%s-%s-%s", "findEntriesByQuery", databaseLocation, input);
        if (databaseResponseCache.containsIdentifier(cacheIdentifier))
            return databaseResponseCache.getByIdentifier(cacheIdentifier);

        final List<Entry> foundEntries = new ArrayList<>();
        final Document query = new MongoFilterQueryParser().parse(new Document(), input);
        if (query == null)
            return cacheDatabaseResponse(cacheIdentifier, DatabaseResponse.FAILED);

        final FindIterable<Document> foundDocuments = getCollection(databaseLocation).find(query).maxTime(15, TimeUnit.SECONDS);
        for (final Document document : foundDocuments) {
            final Action action = Action.parseSafe(document.getString("action"));
            if (action == Action.UNKNOWN) continue;
            foundEntries.add(entryParserRegister.get(action).parse(document));
        }

        return cacheDatabaseResponse(cacheIdentifier, new DatabaseResponse(DatabaseResponse.Status.SUCCEEDED, foundEntries));

    }

    /**
     * Used to find entries in the entire database structure by the provided database query.
     *
     * @param input The database query.
     * @return A DatabaseResponse containing the potential result(s).
     */

    public DatabaseResponse findEntriesByQueryEverywhere(final String input) {

        final String cacheIdentifier = String.format("%s-%s", "findEntriesByQueryEverywhere", input);
        if (databaseResponseCache.containsIdentifier(cacheIdentifier))
            return databaseResponseCache.getByIdentifier(cacheIdentifier);

        final List<Entry> foundEntries = new ArrayList<>();
        final Document query = new MongoFilterQueryParser().parse(new Document(), input);
        if (query == null)
            return cacheDatabaseResponse(cacheIdentifier, DatabaseResponse.FAILED);

        for (final String collectionName : database.listCollectionNames()) {
            final FindIterable<Document> foundDocuments = getCollection(collectionName).find(query).maxTime(15, TimeUnit.SECONDS);
            for (final Document document : foundDocuments) {
                final Action action = Action.parseSafe(document.getString("action"));
                if (action == Action.UNKNOWN) continue;
                foundEntries.add(entryParserRegister.get(action).parse(document));
            }
        }

        return cacheDatabaseResponse(cacheIdentifier, new DatabaseResponse(DatabaseResponse.Status.SUCCEEDED, foundEntries));

    }

    /**
     * Used to find a document by its object id.
     *
     * @param id The object id to look for.
     * @return The first found document with the specified object id.
     */

    @Nullable
    public Document findByIdEverywhere(final String id) {

        final Document query = new Document("_id", new ObjectId(id));
        for (final String collectionName : database.listCollectionNames()) {
            final Document document = getCollection(collectionName).find(query).maxTime(15, TimeUnit.SECONDS).first();
            if (document != null) return document;
        }

        return null;

    }

    /**
     * Used to find a document by its object id in a specific collection.
     *
     * @param collectionName The name of the collection to search in.
     * @param id             The object id to look for.
     * @return The first found document with the specified object id.
     */

    @Nullable
    public Document findDocumentById(final String collectionName, final String id) {

        return getCollection(collectionName).find(new Document("_id", new ObjectId(id))).maxTime(15, TimeUnit.SECONDS).first();

    }

    /**
     * Used to find a document by its uuid.
     *
     * @param uuid The uuid to look for.
     * @return The first found document with the specified uuid.
     */

    @Nullable
    public Document findDocumentByUUIDEverywhere(final UUID uuid) {

        final Document query = new Document("uuid", uuid);
        for (final String collectionName : database.listCollectionNames()) {
            final Document document = getCollection(collectionName).find(query).maxTime(15, TimeUnit.SECONDS).first();
            if (document != null) return document;
        }

        return null;

    }

    /**
     * Used to find a document by its uuid in a specific collection.
     *
     * @param collectionName The name of the collection to search in.
     * @param uuid           The uuid to look for.
     * @return The first found document with the specified uuid.
     */

    @Nullable
    public Document findDocumentByUUID(final String collectionName, final UUID uuid) {

        return getCollection(collectionName).find(new Document("uuid", uuid)).maxTime(15, TimeUnit.SECONDS).first();

    }

    /**
     * Used to cache a DatabaseResponse with the specified identifier.
     *
     * @param identifier       The identifier.
     * @param databaseResponse The DatabaseResponse to cache.
     * @return The cached DatabaseResponse instance.
     */

    public DatabaseResponse cacheDatabaseResponse(final String identifier, final DatabaseResponse databaseResponse) {

        databaseResponseCache.add(identifier, databaseResponse);
        return databaseResponse;

    }

}
