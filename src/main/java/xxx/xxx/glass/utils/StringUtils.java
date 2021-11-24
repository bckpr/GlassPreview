package xxx.xxx.glass.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Provides utility methods for string manipulation and conversion.
 */

public class StringUtils {

    /**
     * Encodes the input to base64.
     *
     * @param input The input.
     * @return The encoded output.
     */

    public static String stringToB64(final String input) {

        final byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes());
        return new String(encodedBytes);

    }

    /**
     * Decodes a b64 encoded input.
     *
     * @param input The input.
     * @return The decoded output.
     */

    public static String b64ToString(final String input) {

        final byte[] decodedBytes = Base64.getDecoder().decode(input.getBytes());
        return new String(decodedBytes);

    }

    /**
     * Surrounds the provided input with the provided prefix and suffix.
     *
     * @param input  The input.
     * @param prefix The prefix that will be added in front of the input.
     * @param suffix The suffix that will be added after the input.
     * @return The combined inputs.
     */

    public static String surround(final String input, final String prefix, final String suffix) {

        return prefix + input + suffix;

    }

    /**
     * Hashes the input with the MD5 algorithm.
     *
     * @param input The input.
     * @return The hashed output.
     */

    public static String hash(final String input) {

        try {
            final byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            final byte[] hashedBytes = messageDigest.digest(inputBytes);
            final StringBuilder builder = new StringBuilder();
            for (byte hashedByte : hashedBytes)
                builder.append(Integer.toHexString((hashedByte & 0xFF) | 0x100).substring(1, 3));
            return builder.toString();
        } catch (final NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }

    }

}
