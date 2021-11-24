package xxx.xxx.glass.command.arguments.validators;

/**
 * Contract for ArgumentValidator implementations.
 */

public interface ArgumentValidator {

    boolean isValid(final String input);

    Class<?> getUnderlyingClass();

}