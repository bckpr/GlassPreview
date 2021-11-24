package xxx.xxx.glass.utils;

import org.jetbrains.annotations.NotNull;

/**
 * Provides utility methods to make inputs more readable.
 */

public class Beautifier {

    /**
     * Converts the provided enum value into a more readable string.
     *
     * @param input The enum value input.
     * @return The more readable output.
     */

    @NotNull
    public static String beautifyEnum(final Enum<?> input) {

        final char[] inputChars = input.name().toCharArray();
        final char[] outputChars = new char[inputChars.length];

        boolean capitalize = true;
        for (int i = 0; i < inputChars.length; i++) {
            final char currentChar = inputChars[i];
            if (currentChar == '_') {
                outputChars[i] = ' ';
                capitalize = true;
            } else {
                outputChars[i] = capitalize ? Character.toUpperCase(currentChar) : Character.toLowerCase(currentChar);
                if (capitalize) capitalize = false;
            }
        }

        return new String(outputChars);

    }

}
