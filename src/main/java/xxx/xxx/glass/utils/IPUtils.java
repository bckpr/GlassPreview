package xxx.xxx.glass.utils;

import java.util.regex.Pattern;

/**
 * Provides utility methods for working with ip addresses.
 */

public class IPUtils {

    private final static Pattern IP_PATTERN = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
    private final static char[] vowels = {'a', 'e', 'i', 'o', 'u'};
    private final static char[] consonants = {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'};
    private final static String[][] parts =
            {
                    {"Rek", "Tok", "Leg", "Mog", "Gon", "Fen", "Tel", "Pen", "Eno", "Cel"},
                    {"mu", "ma", "me", "mi", "mo", "mu", "ma", "me", "mi", "mo"},
                    {"lo", "lu", "la", "le", "li", "lo", "lu", "la", "le", "lo"}
            };

    /**
     * Unused and unsafe utility method, it's supposed to hide
     * the original ip from normal chat moderators while still
     * allowing them to identify a player by it.
     *
     * @param ip The original ip.
     * @return The converted ip.
     */

    public static String convertIpToWords(final String ip) {

        if (!IP_PATTERN.matcher(ip).matches()) return null;

        final StringBuilder builder = new StringBuilder();
        final String[] ipParts = ip.split("\\.");
        for (int i = 0; i < ipParts.length; i++) {
            final String part = ipParts[i];

            int iteration = 0;
            for (final char character : part.toCharArray()) {
                final int index = Character.getNumericValue(character);
                builder.append(parts[iteration][index]);
                iteration++;
            }

            if (i < ipParts.length - 1) builder.append('-');
        }

        return builder.toString();

    }

}
