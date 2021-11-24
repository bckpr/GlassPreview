package xxx.xxx.glass.common.serialization;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;

/**
 * Provides utility methods for string deserialization.
 */

public class StringDeserializer {

    /**
     * Used to deserialize the base64 encoded input to an object.
     *
     * @param input The base64 encoded input.
     * @param <T>   The target type.
     * @return The deserialized object.
     * @throws IOException            Gets thrown if the reading process fails.
     * @throws ClassNotFoundException Gets thrown if the target type class doesn't exist.
     */

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> T deserialize(final String input) throws IOException, ClassNotFoundException {

        final byte[] data = Base64.getDecoder().decode(input);
        final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
        final Object obj = in.readObject();
        in.close();

        return (T) obj;

    }

    /**
     * Used to deserialize the base64 encoded input to an object. Will
     * return <code>null</code> if the deserialization process fails.
     *
     * @param input The base64 encoded input.
     * @param <T>   The target type.
     * @return The deserialized object or <code>null</code> if the process failed.
     */

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T deserializeUnsafe(final String input) {

        try {
            final byte[] data = Base64.getDecoder().decode(input);
            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
            final Object obj = in.readObject();
            in.close();

            return (T) obj;
        } catch (final IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }

    }

}
