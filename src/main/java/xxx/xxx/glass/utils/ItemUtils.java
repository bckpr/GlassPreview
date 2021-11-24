package xxx.xxx.glass.utils;

import net.minecraft.server.v1_12_R1.MojangsonParser;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

/**
 * Provides utility methods for ItemStack instances.
 */

public class ItemUtils {

    /**
     * Used to convert an ItemStack into a json string.
     *
     * @param item The ItemStack input.
     * @return The converted json string.
     */

    public static String itemToJson(final ItemStack item) {

        return CraftItemStack.asNMSCopy(item).save(new NBTTagCompound()).toString();

    }

    /**
     * Used to convert a json string into an ItemStack.
     *
     * @param json The json input.
     * @return The converted ItemStack.
     */

    public static ItemStack jsonToItem(final String json) {

        try {
            final NBTTagCompound nbt = MojangsonParser.parse(json);
            return CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_12_R1.ItemStack(nbt));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    /**
     * Used to get the internal name of an ItemStack.
     *
     * @param item The ItemStack input.
     * @return The internal name of the ItemStack.
     */

    public static String getItemName(final ItemStack item) {

        return CraftItemStack.asNMSCopy(item).getName();

    }

    /**
     * Used to inject an unique id into an ItemStack's NBT data.
     *
     * @param item The ItemStack input.
     * @param uuid The UUID to inject.
     * @return The modified ItemStack.
     */

    public static ItemStack injectUniqueId(final ItemStack item, final UUID uuid) {

        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (!nmsItem.hasTag()) nmsItem.setTag(new NBTTagCompound());
        Objects.requireNonNull(nmsItem.getTag()).setString("glass-uuid", uuid.toString());

        return CraftItemStack.asBukkitCopy(nmsItem);

    }

}
