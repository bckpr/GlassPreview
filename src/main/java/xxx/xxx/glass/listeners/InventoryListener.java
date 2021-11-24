package xxx.xxx.glass.listeners;

import xxx.xxx.glass.common.CacheQueue;
import xxx.xxx.glass.common.Position;
import xxx.xxx.glass.common.ServiceProvider;
import xxx.xxx.glass.data.entry.Entry;
import xxx.xxx.glass.data.entry.InventoryTransactionEntry;
import xxx.xxx.glass.data.entry.User;
import xxx.xxx.glass.wrapped.WrappedItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Listener for inventory action related events.
 */

public class InventoryListener extends ActionListener implements Listener {

    private final static Set<InventoryType> SUPPORTED_INVENTORY_TYPES = new HashSet<>(
            Arrays.asList(
                    InventoryType.BEACON,
                    InventoryType.BREWING,
                    InventoryType.CHEST,
                    InventoryType.DISPENSER,
                    InventoryType.DROPPER,
                    InventoryType.ENDER_CHEST,
                    InventoryType.FURNACE,
                    InventoryType.HOPPER,
                    InventoryType.SHULKER_BOX
            )
    );

    private final ActiveInventoryCollection activeInventories;

    public InventoryListener(final CacheQueue<Entry> queue, final ServiceProvider serviceProvider) {

        super(queue);

        this.activeInventories = serviceProvider.getService(ActiveInventoryCollection.class);

    }

    /**
     * Gets called when an inventory gets opened for a player.
     *
     * @param event The triggered event instance.
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(final InventoryOpenEvent event) {

        if (SUPPORTED_INVENTORY_TYPES.contains(event.getInventory().getType()))
            activeInventories.add(event.getPlayer().getUniqueId(), cloneItems(event.getInventory().getContents()));

    }

    /**
     * Gets called when an open inventory get closed.
     *
     * @param event The triggered event instance.
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(final InventoryCloseEvent event) {

        final Player player = (Player) event.getPlayer();
        if (!activeInventories.contains(player)) return;

        final InventoryType inventoryType = event.getInventory().getType();
        final String inventoryTitle = event.getView().getTitle();

        Location location = player.getLocation();
        if (event.getInventory().getHolder() instanceof Container)
            location = ((Container) event.getInventory().getHolder()).getLocation();

        final ItemStack[] beforeItems = activeInventories.get(player);
        final ItemStack[] afterItems = cloneItems(event.getInventory().getContents());
        final Map<ItemStack, Integer> differences = getContainerContentDifferences(beforeItems, afterItems);

        if (differences.isEmpty()) {
            activeInventories.remove(player);
            return;
        }

        final List<WrappedItemStack> addedItems = new ArrayList<>();
        final List<WrappedItemStack> removedItems = new ArrayList<>();
        for (final Map.Entry<ItemStack, Integer> entry : differences.entrySet()) {
            final int amount = entry.getValue();
            final WrappedItemStack wrappedItemStack = new WrappedItemStack(prepareItem(entry.getKey()), Math.abs(amount));
            if (amount > 0) addedItems.add(wrappedItemStack);
            else removedItems.add(wrappedItemStack);
        }

        final InventoryTransactionEntry inventoryTransactionEntry = new InventoryTransactionEntry(
                UUID.randomUUID(),
                User.fromPlayer(player),
                System.currentTimeMillis(),
                Position.fromLocation(location),
                inventoryTitle,
                inventoryType,
                inventoryTitle,
                addedItems,
                removedItems
        );

        activeInventories.remove(player);

        getQueue().add(inventoryTransactionEntry);

    }

    /**
     * Used to prepare an ItemStack for wrapping.
     *
     * @param input The ItemStack input.
     * @return The prepared ItemStack.
     */

    private ItemStack prepareItem(final ItemStack input) {

        final ItemStack output = input.clone();
        output.setAmount(1);

        return output;

    }

    /**
     * Used to copy the items from the provided ItemStack array into
     * a new array, skips empty positions.
     *
     * @param input The ItemStack array input.
     * @return The cloned ItemStack array.
     */

    private ItemStack[] cloneItems(final ItemStack[] input) {

        final ItemStack[] items = new ItemStack[input.length];
        for (int i = 0; i < input.length; i++) {
            if (input[i] == null) continue;
            items[i] = input[i].clone();
        }

        return items;

    }

    /**
     * Used to get a map that contains the difference between the
     * two provided ItemStack arrays.
     *
     * @param beforeItems The first ItemStack array.
     * @param afterItems  The second ItemStack array.
     * @return The map that contains the difference between the provided arrays.
     */

    private Map<ItemStack, Integer> getContainerContentDifferences(final ItemStack[] beforeItems, final ItemStack[] afterItems) {

        final Map<WrappedItemStack, Integer> beforeMap = new HashMap<>();
        fillItemMap(beforeMap, beforeItems);

        final Map<WrappedItemStack, Integer> afterMap = new HashMap<>();
        fillItemMap(afterMap, afterItems);

        final Map<ItemStack, Integer> differences = new HashMap<>();
        for (final Map.Entry<WrappedItemStack, Integer> entry : beforeMap.entrySet()) {
            if (afterMap.containsKey(entry.getKey())) {
                final int beforeAmount = entry.getValue();
                final int afterAmount = afterMap.get(entry.getKey());
                if (beforeAmount == afterAmount) continue;
                differences.put(entry.getKey().getItem(), afterAmount - beforeAmount);
            } else {
                differences.put(entry.getKey().getItem(), entry.getValue() * -1);
            }
        }

        for (final Map.Entry<WrappedItemStack, Integer> entry : afterMap.entrySet()) {
            if (!beforeMap.containsKey(entry.getKey()))
                differences.put(entry.getKey().getItem(), entry.getValue());
        }

        return differences;

    }

    /**
     * Used to fill the provided maps with the provided ItemStack array,
     * items from the array will get wrapped.
     *
     * @param map   The map to fill.
     * @param items The items to fill in.
     * @see WrappedItemStack
     */

    private void fillItemMap(final Map<WrappedItemStack, Integer> map, final ItemStack[] items) {

        for (final ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR) continue;
            final WrappedItemStack wrappedItem = new WrappedItemStack(item);
            if (map.containsKey(wrappedItem)) {
                map.replace(wrappedItem, map.get(wrappedItem) + item.getAmount());
            } else {
                map.put(new WrappedItemStack(item), item.getAmount());
            }
        }

    }

}
