package xxx.xxx.glass.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Used to register handlers that know how to handle a provided class instance.
 *
 * @param <T> The type.
 */

public class ClassHandler<T> {

    private final Map<Class<? extends T>, Consumer<T>> registeredHandlers = new HashMap<>();

    /**
     * Used to register a handler for the provided class.
     *
     * @param clazz    The class.
     * @param consumer The handler.
     */

    public void registerHandler(@NotNull final Class<? extends T> clazz, @NotNull final Consumer<T> consumer) {

        registeredHandlers.put(clazz, consumer);

    }

    /**
     * Used to check if there is a registered handler for the provided class.
     *
     * @param clazz The class.
     * @return <code>true</code> if a handler was found, <code>false</code> otherwise.
     */

    public boolean contains(@NotNull final Class<? extends T> clazz) {

        return registeredHandlers.containsKey(clazz);

    }

    /**
     * Used to get the registered handler for the provided class, might return
     * null if no handler exists.
     *
     * @param clazz The class.
     * @return The handler or <code>null</code> if no handler exists.
     */

    @Nullable
    public Consumer<T> getHandler(@NotNull final Class<? extends T> clazz) {

        return registeredHandlers.get(clazz);

    }

}
