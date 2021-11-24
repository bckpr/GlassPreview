package xxx.xxx.glass.utils;

import xxx.xxx.glass.common.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides utility methods to make working with maps easier.
 */

public class MapUtils {

    /**
     * Creates a new map instance and fills it with the provided entries.
     *
     * @param entries The entries to put in the map.
     * @param <K>     The key type of the map.
     * @param <V>     The value type of the map.
     * @return The created map instance.
     */

    @SafeVarargs
    public static <K, V> Map<K, V> newMapOf(final Pair<K, V>... entries) {

        final Map<K, V> map = new HashMap<>();
        for (final Pair<K, V> entry : entries)
            map.put(entry.getKey(), entry.getValue());

        return map;

    }

}
