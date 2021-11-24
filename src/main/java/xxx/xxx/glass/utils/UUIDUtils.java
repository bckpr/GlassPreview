package xxx.xxx.glass.utils;

import org.bson.UuidRepresentation;
import org.bson.internal.UuidHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

/**
 * Provides utility methods for UUID instances.
 */

public class UUIDUtils {

    /**
     * Used to convert a UUID to an InputStream for easier
     * and more efficient database usage.
     *
     * @param uuid The UUID to convert.
     * @return The created InputStream instance.
     */

    @NotNull
    public static InputStream uuidToStream(final UUID uuid) {

        final byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return new ByteArrayInputStream(bytes);

    }

    /**
     * Used to convert an InputStream to a UUID for easier
     * and more efficient database usage.
     *
     * @param stream The InputStream.
     * @return The created UUID or null if the creation failed.
     */

    @Nullable
    public static UUID streamToUUID(final InputStream stream) {

        final ByteBuffer buffer = ByteBuffer.allocate(16);
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Math.max(stream.available(), 32));
            final byte[] byteBuffer = new byte[16];
            int read;
            while (true) {
                read = stream.read(byteBuffer);
                if (read == -1) break;
                outputStream.write(byteBuffer, 0, read);
            }

            buffer.put(outputStream.toByteArray());
            buffer.flip();
            return new UUID(buffer.getLong(), buffer.getLong());
        } catch (final IOException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    /**
     * Used to convert a bson b64 string to a UUID. Will throw an exception
     * if the provided input is invalid.
     *
     * @param b64 The bson b64 input.
     * @return The created UUID instance.
     * @see org.bson.BsonSerializationException
     */

    @NotNull
    public static UUID bsonB64ToUUID(final String b64) {

        return UuidHelper.decodeBinaryToUuid(Base64.getDecoder().decode(b64), (byte) 3, UuidRepresentation.JAVA_LEGACY);

    }

}
