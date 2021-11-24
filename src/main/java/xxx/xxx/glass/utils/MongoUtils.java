package xxx.xxx.glass.utils;

import com.mongodb.client.MongoDatabase;

/**
 * Provides utility methods for working with MongoDB databases.
 */

public class MongoUtils {

    /**
     * Checks if the provided database contains a collection with the provided name.
     *
     * @param database The database to look in.
     * @param name     The name of the collection to look for.
     * @return <code>true</code> if a collection with the specified name was found, <code>false</code> otherwise.
     */

    public static boolean hasCollection(final MongoDatabase database, final String name) {

        for (final String collectionName : database.listCollectionNames())
            if (collectionName.equals(name)) return true;

        return false;

    }

}
