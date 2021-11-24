package xxx.xxx.glass.listeners;

import xxx.xxx.glass.common.CacheQueue;
import xxx.xxx.glass.common.Position;
import xxx.xxx.glass.utils.ReflectionUtils;
import xxx.xxx.glass.utils.StringUtils;
import discord4j.store.api.util.Lazy;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import xxx.xxx.glass.data.entry.*;

import java.util.Collection;
import java.util.UUID;

/**
 * Listener for player related events.
 */

public class PlayerListener extends ActionListener implements Listener {

    private final Lazy<Collection<Command>> knownCommands = new Lazy<>(ReflectionUtils::getKnownCommands);

    public PlayerListener(final CacheQueue<Entry> queue) {

        super(queue);

    }

    /**
     * Gets called when a player joins the server.
     *
     * @param event The triggered event instance.
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        final String ip = player.getAddress().getAddress().getHostAddress();
        final String hashedIp = StringUtils.hash(ip);

        final JoinEntry joinEntry = new JoinEntry(
                UUID.randomUUID(),
                User.fromPlayer(player),
                System.currentTimeMillis(),
                Position.fromLocation(player.getLocation()),
                hashedIp,
                ip,
                hashedIp
        );

        getQueue().add(joinEntry);

    }

    /**
     * Gets called when a player executed an unvalidated command.
     *
     * @param event The triggered event instance.
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {

        final Player player = event.getPlayer();
        final String command = event.getMessage().substring(1);
        final String[] parts = command.split(" ");

        Command foundCommand = null;
        for (final Command knownCommand : knownCommands.get()) {
            if (knownCommand.getName().equalsIgnoreCase(parts[0])) {
                foundCommand = knownCommand;
                break;
            } else {
                for (final String alias : knownCommand.getAliases()) {
                    if (alias.equalsIgnoreCase(parts[0])) {
                        foundCommand = knownCommand;
                        break;
                    }
                }
            }
        }

        if (foundCommand == null) return;

        final CommandEntry commandEntry = new CommandEntry(
                UUID.randomUUID(),
                User.fromPlayer(player),
                System.currentTimeMillis(),
                Position.fromLocation(player.getLocation()),
                parts[0].toUpperCase(),
                command
        );

        getQueue().add(commandEntry);

    }

    /**
     * Gets called when a player send a message.
     *
     * @param event The triggered event instance.
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {

        final Player player = event.getPlayer();
        final String message = event.getMessage();

        final ChatMessageEntry chatMessageEntry = new ChatMessageEntry(
                UUID.randomUUID(),
                User.fromPlayer(player),
                System.currentTimeMillis(),
                Position.fromLocation(player.getLocation()),
                message,
                message
        );

        getQueue().add(chatMessageEntry);

    }

    /**
     * Gets called when a player drops an item.
     *
     * @param event The triggered event instance.
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {

        final Player player = event.getPlayer();
        final ItemStack item = event.getItemDrop().getItemStack();

        final DropItemEntry dropItemEntry = new DropItemEntry(
                UUID.randomUUID(),
                User.fromPlayer(player),
                System.currentTimeMillis(),
                Position.fromLocation(player.getLocation()),
                item.getType().name(),
                item,
                item.getAmount()
        );

        getQueue().add(dropItemEntry);

    }

    /**
     * Gets called when an entity picks up an item.
     *
     * @param event The triggered event instance.
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPickupItem(final EntityPickupItemEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        final Player player = (Player) event.getEntity();
        final ItemStack item = event.getItem().getItemStack();

        final PickupItemEntry pickupItemEntry = new PickupItemEntry(
                UUID.randomUUID(),
                User.fromPlayer(player),
                System.currentTimeMillis(),
                Position.fromLocation(player.getLocation()),
                item.getType().name(),
                item,
                item.getAmount()
        );

        getQueue().add(pickupItemEntry);

    }

}