package xxx.xxx.glass.listeners;

import xxx.xxx.glass.common.CacheQueue;
import xxx.xxx.glass.common.Position;
import xxx.xxx.glass.data.entry.BlockBreakEntry;
import xxx.xxx.glass.data.entry.BlockPlaceEntry;
import xxx.xxx.glass.data.entry.Entry;
import xxx.xxx.glass.data.entry.User;
import xxx.xxx.glass.wrapped.WrappedMaterialData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

/**
 * Listener for block related events.
 */

public class BlockListener extends ActionListener implements Listener {

    public BlockListener(final CacheQueue<Entry> queue) {

        super(queue);

    }

    /**
     * Gets called when a player breaks a block.
     *
     * @param event The triggered event instance.
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        final WrappedMaterialData blockType = new WrappedMaterialData(block.getState().getData());
        final int xp = event.getExpToDrop();

        final BlockBreakEntry blockBreakEntry = new BlockBreakEntry(
                UUID.randomUUID(),
                User.fromPlayer(player),
                System.currentTimeMillis(),
                Position.fromLocation(block.getLocation()),
                blockType.toString(),
                blockType,
                xp
        );

        getQueue().add(blockBreakEntry);

    }

    /**
     * Gets called when a player places a block.
     *
     * @param event The triggered event instance.
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {

        if (!event.canBuild()) return;

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        final WrappedMaterialData oldBlockType = new WrappedMaterialData(event.getBlockReplacedState().getData());
        final WrappedMaterialData newBlockType = new WrappedMaterialData(block.getState().getData());

        final BlockPlaceEntry blockPlaceEntry = new BlockPlaceEntry(
                UUID.randomUUID(),
                User.fromPlayer(player),
                System.currentTimeMillis(),
                Position.fromLocation(block.getLocation()),
                oldBlockType + "=" + newBlockType,
                oldBlockType,
                newBlockType
        );

        getQueue().add(blockPlaceEntry);

    }

}
