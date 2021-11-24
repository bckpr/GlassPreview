package xxx.xxx.glass.wrapped;

import xxx.xxx.glass.command.arguments.validators.IntegerArgumentValidator;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Wrapped version of the MaterialData class for easier (de)serialization
 * and database compatibility.
 *
 * @see MaterialData
 */

@SuppressWarnings("deprecation")
public class WrappedMaterialData implements Serializable {

    private final static long serialVersionUID = 1;

    private MaterialData materialData;

    public WrappedMaterialData(final MaterialData materialData) {

        this.materialData = materialData;

    }

    /**
     * Used to return the underlying MaterialData instance.
     *
     * @return The underlying MaterialData instance.
     */

    public MaterialData getMaterialData() {

        return materialData;

    }

    /**
     * Used to serialize the WrappedMaterialData instance.
     *
     * @param out Output stream to write the data in.
     * @throws IOException Gets thrown when the writing process fails.
     */

    private void writeObject(final ObjectOutputStream out) throws IOException {

        out.writeInt(materialData.getItemTypeId());
        out.writeByte(materialData.getData());

    }

    /**
     * Used to deserialize the provided stream.
     *
     * @param in Input stream containing the data.
     * @throws IOException Gets thrown when the reading process fails.
     */

    private void readObject(final ObjectInputStream in) throws IOException {

        final int itemTypeId = in.readInt();
        final byte data = in.readByte();

        this.materialData = new MaterialData(itemTypeId, data);

    }

    /**
     * Converts the WrappedMaterialData instance to a string that
     * can later be parsed using the #fromString(String) method.
     *
     * @return The converted WrappedMaterialData instance.
     * @see WrappedMaterialData#fromString(String)
     */

    @Override
    public String toString() {

        return String.format("%d:%d", materialData.getItemTypeId(), materialData.getData());

    }

    /**
     * Used to create a new WrappedMaterialData instance based on the provided
     * input, the required format supports data less material types.
     *
     * @param input The material data in a type(:data) string format.
     * @return A new WrappedMaterialData instance based on the provided input.
     */

    @Nullable
    public static WrappedMaterialData fromString(final String input) {

        final IntegerArgumentValidator integerArgumentValidator = IntegerArgumentValidator.getInstance();

        if (input.contains(":")) {
            final String[] parts = input.split(":");
            final String itemTypeId = parts[0];
            final String data = parts[1];

            if (!integerArgumentValidator.isValid(data)) return null;

            MaterialData materialData;
            if (integerArgumentValidator.isValid(itemTypeId)) {
                materialData = new MaterialData(Integer.parseInt(itemTypeId), Byte.parseByte(data));
            } else {
                final Material material = Material.matchMaterial(itemTypeId);
                if (material == null) return null;
                materialData = new MaterialData(material, Byte.parseByte(data));
            }

            return new WrappedMaterialData(materialData);
        } else {
            if (integerArgumentValidator.isValid(input)) {
                return new WrappedMaterialData(new MaterialData(Integer.parseInt(input)));
            } else {
                final Material material = Material.matchMaterial(input);
                if (material == null) return null;
                return new WrappedMaterialData(new MaterialData(material, (byte) 0));
            }
        }

    }

}
