package xxx.xxx.glass.data.parser;

import xxx.xxx.glass.data.entry.Action;
import xxx.xxx.glass.data.entry.Entry;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to register and access action parsers.
 */

public class EntryParserRegister {

    private final Map<Action, ClassParser<? extends Entry>> registeredParsers = new HashMap<>();

    public void register(final Action action, ClassParser<? extends Entry> classParser) {

        registeredParsers.put(action, classParser);

    }

    /**
     * Used to get a registered action parser.
     *
     * @param action The action.
     * @return The found parser or null if not found.
     */

    public ClassParser<? extends Entry> get(final Action action) {

        return registeredParsers.getOrDefault(action, null);

    }

    /**
     * Used to get a registered action parser. This
     * method accepts a default parser.
     *
     * @param action       The action.
     * @param defaultValue The default parser.
     * @return The found parser or the specified default one if not found.
     */

    public ClassParser<? extends Entry> get(final Action action, final ClassParser<? extends Entry> defaultValue) {

        return registeredParsers.getOrDefault(action, defaultValue);

    }

}
