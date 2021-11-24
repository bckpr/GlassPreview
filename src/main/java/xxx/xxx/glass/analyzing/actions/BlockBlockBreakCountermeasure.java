package xxx.xxx.glass.analyzing.actions;

import com.google.common.collect.Sets;
import xxx.xxx.glass.common.TimeSpan;
import xxx.xxx.glass.utils.Beautifier;
import xxx.xxx.glass.utils.TimeUtils;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.MaterialData;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Countermeasure that blocks the player from placing blocks.
 */

public class BlockBlockBreakCountermeasure implements Countermeasure {

    private final TimeSpan timeSpan;
    private final Set<MaterialData> blockTypes;
    private final Map<UUID, Boolean> blockedPlayers;

    public BlockBlockBreakCountermeasure(final TimeSpan timeSpan, final MaterialData... blockTypes) {

        this.timeSpan = timeSpan;
        this.blockTypes = Sets.newHashSet(blockTypes);
        this.blockedPlayers = ExpiringMap.builder()
                .expiration(timeSpan.getMillis(), TimeUnit.MILLISECONDS)
                .build();

    }

    public BlockBlockBreakCountermeasure(final TimeSpan timeSpan, final Set<MaterialData> blockTypes) {

        this.timeSpan = timeSpan;
        this.blockTypes = blockTypes;
        this.blockedPlayers = ExpiringMap.builder()
                .expiration(timeSpan.getMillis(), TimeUnit.MILLISECONDS)
                .build();

    }

    /**
     * Used to execute the countermeasure.
     *
     * @param target The target craft player.
     */

    @Override
    public void execute(final Player target) {

        blockedPlayers.put(target.getUniqueId(), true);

    }

    /**
     * Used to cancel the countermeasure before it expired.
     *
     * @param target The target craft player.
     */

    @Override
    public void cancel(final Player target) {

        blockedPlayers.remove(target.getUniqueId());

    }

    /**
     * Gets called when a new event gets fired.
     *
     * @param event The triggered event instance.
     */

    @Override
    public void onEvent(final Event event) {

        if (event instanceof BlockBreakEvent)
            onBlockBreak((BlockBreakEvent) event);

    }

    /**
     * Gets called by the #onEvent method when a player
     * breaks a block. Potentially cancels the event.
     *
     * @param event The triggered event instance.
     */

    public void onBlockBreak(final BlockBreakEvent event) {

        if (blockedPlayers.containsKey(event.getPlayer().getUniqueId()))
            event.setCancelled(true);

    }

    /**
     * Used to return the description of the countermeasure.
     *
     * @return The description.
     */

    @Override
    public String getDescription() {

        return "Block the player from breaking " + generateBlockTypeList() + " for " + TimeUtils.convertSecondsToFormattedTime(timeSpan.getSeconds(), true) + ".";

    }

    /**
     * Internal method to generate a readable string list of block types.
     *
     * @return The readable string.
     */

    private String generateBlockTypeList() {

        final StringBuilder builder = new StringBuilder();
        final List<Material> materials = getBlockTypes().stream().map(MaterialData::getItemType).collect(Collectors.toList());
        for (int i = 0; i < materials.size(); i++) {
            final Material material = materials.get(i);
            builder.append(Beautifier.beautifyEnum(material));
            if (materials.size() > 1) {
                if (i == materials.size() - 2) builder.append(" and ");
                else if (i < materials.size() - 1) builder.append(", ");
            }
        }

        return builder.toString();

    }

    /**
     * Used to get the TimeSpan for how long a player gets blocked from
     * placing blocks by default.
     *
     * @return The TimeSpan instance.
     */

    public TimeSpan getTimeSpan() {

        return timeSpan;

    }

    /**
     * Used to get the set of affected block types.
     *
     * @return Set of affected block types.
     */

    public Set<MaterialData> getBlockTypes() {

        return blockTypes;

    }

}
