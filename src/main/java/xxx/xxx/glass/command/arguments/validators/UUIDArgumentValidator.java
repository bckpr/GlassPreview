package xxx.xxx.glass.command.arguments.validators;

import java.util.UUID;

public class UUIDArgumentValidator implements ArgumentValidator {

    /**
     * Safe singleton implementation.
     */

    private static class UUIDArgumentValidatorSingleton {

        private static final UUIDArgumentValidator INSTANCE = new UUIDArgumentValidator();

    }

    private UUIDArgumentValidator() {

    }

    /**
     * Used to check if the provided input is valid.
     *
     * @param input The input.
     * @return <code>true</code> if the input is valid, <code>false</code> otherwise.
     */

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public boolean isValid(String input) {

        try {
            UUID.fromString(input);
            return true;
        } catch (final IllegalArgumentException ex) {
            return false;
        }

    }

    /**
     * Used to get the underlying class.
     *
     * @return The underlying class.
     */

    @Override
    public Class<?> getUnderlyingClass() {

        return UUID.class;

    }

    /**
     * Used to get the singleton instance.
     *
     * @return The singleton instance.
     */

    public static UUIDArgumentValidator getInstance() {

        return UUIDArgumentValidatorSingleton.INSTANCE;

    }

}
