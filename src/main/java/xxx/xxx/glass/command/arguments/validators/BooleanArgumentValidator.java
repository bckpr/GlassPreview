package xxx.xxx.glass.command.arguments.validators;

/**
 * Boolean validator.
 */

public class BooleanArgumentValidator implements ArgumentValidator {

    /**
     * Safe singleton implementation.
     */

    private static class BooleanArgumentValidatorSingleton {

        private static final BooleanArgumentValidator INSTANCE = new BooleanArgumentValidator();

    }

    private BooleanArgumentValidator() {

    }

    /**
     * Used to check if the provided input is valid.
     *
     * @param input The input.
     * @return <code>true</code> if the input is valid, <code>false</code> otherwise.
     */

    @Override
    public boolean isValid(final String input) {

        return (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false"));

    }

    /**
     * Used to get the underlying class.
     *
     * @return The underlying class.
     */

    @Override
    public Class<?> getUnderlyingClass() {

        return Boolean.class;

    }

    /**
     * Used to get the singleton instance.
     *
     * @return The singleton instance.
     */

    public static BooleanArgumentValidator getInstance() {

        return BooleanArgumentValidatorSingleton.INSTANCE;

    }

}