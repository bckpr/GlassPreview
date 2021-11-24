package xxx.xxx.glass.database;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Abstract class to register parser functions for user provided query inputs.
 * @param <T> Output object type.
 * @param <D> Work object type.
 */

abstract class FilterQueryParser<T, D> {

    final static Pattern PART_PATTERN = Pattern.compile("([\\w]+)=([^ ]+)");

    private final Map<String, Function<String, Boolean>> registeredParsers = new HashMap<>();

    /**
     * Used to register a parsing function for a specific identifier.
     *
     * @param identifier The string identifier.
     * @param function   The parsing function.
     */

    protected void registerParser(final String identifier, final Function<String, Boolean> function) {

        registeredParsers.put(identifier, function);

    }

    /**
     * Used to get a parse function from the internal map for the specified
     * identifier if present.
     *
     * @param identifier The string identifier;
     * @return The parse function if found.
     */

    @Nullable
    public Function<String, Boolean> getParser(final String identifier) {

        return registeredParsers.get(identifier);

    }

    /**
     * Tries to parse the specified string input and apply its
     * content to the specified work object.
     *
     * @param workObj The object to work on.
     * @param input   The input data.
     * @return The parsed object.
     */

    @Nullable
    public abstract T parse(final D workObj, final String input);

}
