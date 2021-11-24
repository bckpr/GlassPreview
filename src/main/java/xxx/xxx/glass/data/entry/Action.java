package xxx.xxx.glass.data.entry;

/**
 * Enum for all valid actions that can be logged.
 */

public enum Action {

    DROP_ITEM,
    PICKUP_ITEM,
    CONTAINER_MODIFICATION,
    INVENTORY_TRANSACTION,
    CHAT_MESSAGE,
    BLOCK_BREAK,
    BLOCK_PLACE,
    COMMAND,
    JOIN,
    ISLAND,
    UNKNOWN;

    /**
     * Used to safely parse a string input, if no value was found it
     * will return a value of type UNKNOWN. The parsing process is
     * case insensitive.
     *
     * @param input The input string to parse.
     * @return The found enum value.
     * @see Action#UNKNOWN
     */

    public static Action parseSafe(final String input) {

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

    public static Action parseSafe(final String input, final Action defaultValue) {

        for (final Action action : Action.values()) {
            if (action.name().equalsIgnoreCase(input))
                return action;
        }

        return defaultValue;

    }

}
