package xxx.xxx.glass.command.arguments.validators;

public class StringArgumentValidator implements ArgumentValidator {

    /**
     * Safe singleton implementation.
     */

    private static class StringArgumentValidatorSingleton {

        private static final StringArgumentValidator INSTANCE = new StringArgumentValidator();

    }

    private StringArgumentValidator() {

    }

    /**
     * Used to check if the provided input is valid.
     *
     * @param input The input.
     * @return <code>true</code> if the input is valid, <code>false</code> otherwise.
     */

    @Override
    public boolean isValid(final String input) {

        return input.length() > 0;

    }

    /**
     * Used to get the underlying class.
     *
     * @return The underlying class.
     */

    @Override
    public Class<?> getUnderlyingClass() {

        return String.class;

    }

    /**
     * Used to get the singleton instance.
     *
     * @return The singleton instance.
     */

    public static StringArgumentValidator getInstance() {

        return StringArgumentValidatorSingleton.INSTANCE;

    }

}