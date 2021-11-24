package xxx.xxx.glass.data.parser;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Parses a generic input into a generic output using registered functions.
 *
 * @param <T> The output type.
 */

public abstract class ClassParser<T> {

    private final Map<Class<?>, Function> registeredParsers = new HashMap<>();

    /**
     * Used to register a parsing function for a specific class.
     *
     * @param clazz    The class type.
     * @param function The parsing function.
     * @param <I>      The type of the input data.
     */

    protected <I> void register(final Class<I> clazz, final Function<I, T> function) {

        registeredParsers.put(clazz, function);

    }

    /**
     * Used to get a parse function from the internal map for the specified
     * class if present.
     *
     * @param clazz The class.
     * @return The parse function if found.
     */

    @Nullable
    public Function get(final Class<?> clazz) {

        return registeredParsers.get(clazz);

    }

    /**
     * Looks up if the parser has a parsing function for the specified input
     * object and if so calls it and returns the parsed object.
     *
     * @param input Input class instance that should be parsed.
     * @param <I>   Type of the input class.
     * @return The parsed object.
     */

    @SuppressWarnings("unchecked")
    @Nullable
    public <I> T parse(final I input) {

        final Function<I, T> function = get(input.getClass());
        return (function != null) ? function.apply(input) : null;

    }

}
