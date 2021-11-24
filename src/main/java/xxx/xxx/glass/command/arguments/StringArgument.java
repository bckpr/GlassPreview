package xxx.xxx.glass.command.arguments;

import java.util.UUID;

/**
 * Argument implementation for simple string arguments.
 */

public class StringArgument implements Argument {

    private final String input;

    public StringArgument(final String input) {

        this.input = input;

    }

    @Override
    public boolean getAsBoolean() {

        return Boolean.parseBoolean(input);

    }

    @Override
    public int getAsInteger() {

        return Integer.parseInt(input);

    }

    @Override
    public String getAsString() {

        return input;

    }

    @Override
    public UUID getAsUUID() {

        return UUID.fromString(input);

    }

}
