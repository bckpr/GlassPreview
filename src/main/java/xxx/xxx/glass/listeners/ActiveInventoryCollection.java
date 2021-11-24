package xxx.xxx.glass.listeners;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Maps a players unique id to the items on an open inventory.
 */

public class ActiveInventoryCollection {

    private final Map<UUID, ItemStack[]> activeInventories = new HashMap<>();

    /**
     * Used to internally map a players unique id to an ItemStack array.
     *
     * @param uuid  The players unique id.
     * @param items The current items.
     */

    public void add(final UUID uuid, final ItemStack[] items) {

        activeInventories.put(uuid, items);

    }

    /**
     * Used to internally map a players unique id to an ItemStack array.
     *
     * @param player    The player.
     * @param inventory The inventory to extract the items from.
     */

    public void add(final Player player, final Inventory inventory) {

        add(player.getUniqueId(), inventory.getContents());

    }

    /**
     * Used to remove a player unique id mapping.
     *
     * @param uuid The unique id to remove.
     */

    public void remove(final UUID uuid) {

        activeInventories.remove(uuid);

    }

    /**
     * Used to remove a player unique id mapping.
     *
     * @param player The player.
     */

    public void remove(final Player player) {

        remove(player.getUniqueId());

    }

    /**
     * Used to get a mapped ItemStack array by a player uuid.
     *
     * @param uuid The player uuid.
     * @return The found ItemStack array or null if no mapping exists.
     */

    @Nullable
    public ItemStack[] get(final UUID uuid) {

        return activeInventories.get(uuid);

    }

    /**
     * Used to get a mapped ItemStack array by a player.
     *
     * @param player The player.
     * @return The found ItemStack array or null if no mapping exists.
     */

    @Nullable
    public ItemStack[] get(final Player player) {

        return get(player.getUniqueId());

    }

    /**
     * Used to check if the underlying map contains an entry for the
     * provided unique id.
     *
     * @param uuid The unique id.
     * @return <code>true</code> if a mapping was found, <code>false</code> otherwise.
     */

    public boolean contains(final UUID uuid) {

        return activeInventories.containsKey(uuid);

    }

    /**
     * Used to check if the underlying map contains an entry for the
     * provided player.
     *
     * @param player The player.
     * @return <code>true</code> if a mapping was found, <code>false</code> otherwise.
     */

    public boolean contains(final Player player) {

        return contains(player.getUniqueId());

    }

}
