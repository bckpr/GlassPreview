package xxx.xxx.glass.data.entry;

import org.jetbrains.annotations.NotNull;

/**
 * Enum for sub actions, currently only used by ContainerModificationEntry instances which are deprecated.
 * @see ContainerModificationEntry
 */

public enum SubAction {

    ADD_ITEM,
    REMOVE_ITEM,
    UNKNOWN;

    /**
     * Used to safely parse a string input, if no value was found it
     * will return a value of type UNKNOWN. The parsing process is
     * case insensitive.
     *
     * @param input The input string to parse.
     * @return The found enum value.
     * @see SubAction#UNKNOWN
     */

    @NotNull
    public static SubAction parseSafe(@NotNull final String input) {

        return parseSafe(input, UNKNOWN);

    }

    /**
     * Used to safely parse a string input, if no value was found it
     * will return the specified default value. The parsing process
     * is case insensitive.
     *
     * @param input        The input string to parse.
     * @param defaultValue The default value.
     * @return The found enum value.
     */

    @NotNull
    public static SubAction parseSafe(@NotNull final String input, @NotNull final SubAction defaultValue) {

        for (final SubAction action : SubAction.values()) {
            if (action.name().equalsIgnoreCase(input))
                return action;
        }

        return defaultValue;

    }

}
