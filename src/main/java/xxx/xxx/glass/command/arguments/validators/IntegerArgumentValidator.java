package xxx.xxx.glass.command.arguments.validators;

public class IntegerArgumentValidator implements ArgumentValidator {

    /**
     * Safe singleton implementation.
     */

    private static class IntegerArgumentValidatorSingleton {

        private static final IntegerArgumentValidator INSTANCE = new IntegerArgumentValidator();

    }

    private IntegerArgumentValidator() {

    }

    /**
     * Used to check if the provided input is valid.
     *
     * @param input The input.
     * @return <code>true</code> if the input is valid, <code>false</code> otherwise.
     */

    @Override
    public boolean isValid(String input) {

        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException ex) {
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

        return Integer.class;

    }

    /**
     * Used to get the singleton instance.
     *
     * @return The singleton instance.
     */

    public static IntegerArgumentValidator getInstance() {

        return IntegerArgumentValidatorSingleton.INSTANCE;

    }

}