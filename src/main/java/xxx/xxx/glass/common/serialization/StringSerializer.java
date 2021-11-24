package xxx.xxx.glass.common.serialization;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Base64;

/**
 * Provides utility methods for string serialization.
 */

public class StringSerializer {

    /**
     * Used to serialize the input object to a base64 encoded string.
     *
     * @param input The serializable input.
     * @return The base64 encoded string.
     * @throws IOException Gets thrown if the writing process fails.
     */

    @NotNull
    public static String serialize(@NotNull final Serializable input) throws IOException {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream objectOut = new ObjectOutputStream(out);
        objectOut.writeObject(input);
        objectOut.close();

        return Base64.getEncoder().encodeToString(out.toByteArray());

    }

    /**
     * Used to serialize the input object to a base64 encoded string. Will
     * return <code>null</code> if the serialization process fails.
     *
     * @param input The serializable input.
     * @return The base64 encoded string or <code>null</code> if the process failed.
     */

    @Nullable
    public static String serializeUnsafe(@NotNull final Serializable input) {

        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectOutputStream objectOut = new ObjectOutputStream(out);
            objectOut.writeObject(input);
            objectOut.close();

            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (final IOException ex) {
            ex.printStackTrace();
            return null;
        }

    }

}
