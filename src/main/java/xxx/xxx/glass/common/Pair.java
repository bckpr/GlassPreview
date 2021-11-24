package xxx.xxx.glass.common;

/**
 * A read-only key-value pair implementation.
 *
 * @param <K> Type of the key.
 * @param <V> Type of the value.
 */

public class Pair<K, V> {

    private final K key;
    private final V value;

    public Pair(K key, V value) {

        this.key = key;
        this.value = value;

    }

    /**
     * Used to get the key.
     *
     * @return The key.
     */

    public K getKey() {

        return key;

    }

    /**
     * Used to get the value.
     *
     * @return The value.
     */

    public V getValue() {

        return value;

    }

    /**
     * Static factory method to create an new instance based on the
     * provided key and value objects.
     *
     * @param key   The key object.
     * @param value The value object.
     * @param <K>   The key type.
     * @param <V>   The value type.
     * @return The created instance.
     */

    public static <K, V> Pair<K, V> of(final K key, final V value) {

        return new Pair<>(key, value);

    }

}
