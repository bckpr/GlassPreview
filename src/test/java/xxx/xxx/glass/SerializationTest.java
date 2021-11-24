package xxx.xxx.glass;

import xxx.xxx.glass.common.serialization.StringDeserializer;
import xxx.xxx.glass.common.serialization.StringSerializer;
import xxx.xxx.glass.wrapped.WrappedMaterialData;
import org.bukkit.material.MaterialData;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("deprecation")
public class SerializationTest {

    private final static Logger LOGGER = Logger.getLogger("test");

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {

        final MaterialData materialData = new MaterialData(1, (byte) 1);
        assertEquals("STONE(1)", materialData.toString());

        final WrappedMaterialData wrappedMaterialData = new WrappedMaterialData(materialData);
        assertEquals("STONE(1)", wrappedMaterialData.getMaterialData().toString());

        final String serialized = StringSerializer.serialize(wrappedMaterialData);
        assertEquals("rO0ABXNyAC9jb20ubWluZXZlcnNlLmdsYXNzLndyYXBwZWQuV3JhcHBlZE1hdGVyaWFsRGF0YQAAAAAAAAABAwABTAAMbWF0ZXJpYWxEYXRhdAAiTG9yZy9idWtraXQvbWF0ZXJpYWwvTWF0ZXJpYWxEYXRhO3hwdwUAAAABAXg=", serialized);

        final String serializedUnsafe = StringSerializer.serializeUnsafe(wrappedMaterialData);
        assertEquals("rO0ABXNyAC9jb20ubWluZXZlcnNlLmdsYXNzLndyYXBwZWQuV3JhcHBlZE1hdGVyaWFsRGF0YQAAAAAAAAABAwABTAAMbWF0ZXJpYWxEYXRhdAAiTG9yZy9idWtraXQvbWF0ZXJpYWwvTWF0ZXJpYWxEYXRhO3hwdwUAAAABAXg=", serializedUnsafe);

    }

    @Test
    public void testDeserialization() throws IOException, ClassNotFoundException {

        final String serialized = "rO0ABXNyAC9jb20ubWluZXZlcnNlLmdsYXNzLndyYXBwZWQuV3JhcHBlZE1hdGVyaWFsRGF0YQAAAAAAAAABAwABTAAMbWF0ZXJpYWxEYXRhdAAiTG9yZy9idWtraXQvbWF0ZXJpYWwvTWF0ZXJpYWxEYXRhO3hwdwUAAAABAXg=";
        final WrappedMaterialData deserializedWrappedMaterialData = StringDeserializer.deserialize(serialized);
        assertEquals("STONE(1)", deserializedWrappedMaterialData.getMaterialData().toString());

        final WrappedMaterialData deserializedWrappedMaterialDataUnsafe = StringDeserializer.deserializeUnsafe(serialized);
        assertNotNull(deserializedWrappedMaterialDataUnsafe);
        assertEquals("STONE(1)", deserializedWrappedMaterialDataUnsafe.getMaterialData().toString());

    }

}
