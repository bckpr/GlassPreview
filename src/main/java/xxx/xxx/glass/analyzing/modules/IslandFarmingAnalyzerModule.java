package xxx.xxx.glass.analyzing.modules;

import xxx.xxx.glass.analyzing.actions.BlockBlockBreakCountermeasure;
import xxx.xxx.glass.analyzing.actions.CountermeasureManager;
import xxx.xxx.glass.command.channel.CommunicationChannel;
import xxx.xxx.glass.command.message.DiscordComplexMessage;
import xxx.xxx.glass.common.ServiceProvider;
import xxx.xxx.glass.common.TimeSpan;
import xxx.xxx.glass.data.entry.*;
import xxx.xxx.glass.discord.ButtonRegistry;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.MessageCreateSpec;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Analyzing module to detect island farming.
 */

public class IslandFarmingAnalyzerModule extends EntryAnalyzerModule {

    {
        registerHandler(BlockBreakEntry.class, this::handleBlockBreakEntry);
        registerHandler(BlockPlaceEntry.class, this::handleBlockPlaceEntry);
        registerHandler(IslandEntry.class, this::handleIslandEntry);
    }

    private final static Set<Material> ISLAND_FARMING_MATERIALS = new HashSet<>(Arrays.asList(Material.GRASS, Material.DIRT));
    private final static long MEDIAN_REFRESH_DELAY = 10_000L;
    private final static int MINIMUM_SUSPICION_LEVEL = 160;

    private final Map<UUID, Boolean> suspiciousPlayers = ExpiringMap.builder()
            .expiration(1, TimeUnit.HOURS)
            .build();
    private final Map<UUID, Map<PerformedAction, Integer>> trackedPlayers = new HashMap<>();
    private final Map<UUID, Boolean> notified = ExpiringMap.builder()
            .expiration(6, TimeUnit.HOURS)
            .build();

    private final ButtonRegistry buttonRegistry;

    private int levelMedian = 0;
    private long levelMedianExpirationTimestamp = 0L;

    public IslandFarmingAnalyzerModule(final CommunicationChannel notificationChannel, final ServiceProvider serviceProvider) {


        super(
                notificationChannel,
                new BlockBlockBreakCountermeasure(TimeSpan.fromHours(1), ISLAND_FARMING_MATERIALS.stream().map(MaterialData::new).collect(Collectors.toSet()))
        );

        this.buttonRegistry = serviceProvider.getService(ButtonRegistry.class);

        serviceProvider.getService(CountermeasureManager.class).registerCountermeasure(getCountermeasure());

    }

    /**
     * Used to send a notification to the configured notification discord channel.
     *
     * @param target       The player to inform about.
     * @param placeholders The placeholders.
     */

    @Override
    public void sendNotification(final Player target, final Map<String, String> placeholders) {

        final String targetName = target != null ? target.getName() : "Unknown";
        final MessageCreateSpec messageCreateSpec = getMessageCreateSpec("Potential Island Farming detected!", String.format("Example Description: %s", targetName));
        final String claimId = UUID.randomUUID().toString();
        final String performActionId = UUID.randomUUID().toString();

        messageCreateSpec.setComponents(ActionRow.of(Button.primary(claimId, "Claim report"), Button.secondary(performActionId, "Perform suggested action")));

        getNotificationChannel().sendComplexMessage(new DiscordComplexMessage(messageCreateSpec.asRequest()));

        buttonRegistry.registerButton(claimId, (buttonInteractEvent) -> {
            this.handleClaimButton(buttonInteractEvent);
            buttonRegistry.unregisterButton(claimId);
        });

        buttonRegistry.registerButton(performActionId, (buttonInteractEvent -> {
            super.handlePerformActionButton(buttonInteractEvent);
            if (target != null) getCountermeasure().execute(target);
            buttonRegistry.unregisterButton(performActionId);
        }));

    }

    /**
     * Internal method that adjusts the level of the provided player uuid based
     * on the provided adjustment value.
     *
     * @param uuid            The unique id of the player.
     * @param action          The action.
     * @param adjustmentValue The value.
     */

    private void adjustLevelAndCheck(final UUID uuid, final Action action, final int adjustmentValue) {

        if (trackedPlayers.containsKey(uuid)) {
            final Map<PerformedAction, Integer> performedActions = trackedPlayers.get(uuid);
            performedActions.put(new PerformedAction(action), adjustmentValue);
        } else {
            final Map<PerformedAction, Integer> performedActions = ExpiringMap.builder()
                    .expiration(30, TimeUnit.MINUTES)
                    .build();
            performedActions.put(new PerformedAction(action), adjustmentValue);
            trackedPlayers.put(uuid, performedActions);
        }

        if (!suspiciousPlayers.containsKey(uuid)) return;

        int level = 0;
        final Map<PerformedAction, Integer> performedActions = trackedPlayers.get(uuid);
        for (final int value : performedActions.values())
            level += value;

        if (System.currentTimeMillis() >= levelMedianExpirationTimestamp) {
            final List<Integer> levels = new ArrayList<>();
            for (final Map<PerformedAction, Integer> innerPerformedActions : trackedPlayers.values()) {
                int innerLevel = 0;
                for (final Integer value : innerPerformedActions.values())
                    innerLevel += value;
                levels.add(innerLevel);
            }

            levels.sort(Integer::compareTo);
            levelMedian = levels.get((int) Math.ceil((levels.size() - 1) / 2.0));
            levelMedianExpirationTimestamp = System.currentTimeMillis() + MEDIAN_REFRESH_DELAY;
        }

        final int suspicionLevel = Math.max(levelMedian, MINIMUM_SUSPICION_LEVEL);
        if (level < suspicionLevel) return;

        if (notified.containsKey(uuid)) return;

        final Player player = Bukkit.getPlayer(uuid);
        final String playerName = player != null ? player.getName() : uuid.toString();

        sendNotification(player, null);
        notified.put(uuid, true);

    }

    /**
     * Used to get the count of performed actions for the provided unique id.
     * Will return null if the unique id couldn't be found.
     *
     * @param uuid The unique id of the player.
     * @return The count of performed actions or 0 if non were found.
     */

    private int getPerformedActionsCount(final UUID uuid) {

        return trackedPlayers.containsKey(uuid) ? trackedPlayers.get(uuid).size() : 0;

    }

    /**
     * Used to handle a BlockBreakEntry.
     *
     * @param input The entry.
     */

    public void handleBlockBreakEntry(final Entry input) {

        final BlockBreakEntry entry = (BlockBreakEntry) input;
        final MaterialData materialData = entry.getBlockType().getMaterialData();

        if (!ISLAND_FARMING_MATERIALS.contains(materialData.getItemType())) return;

        adjustLevelAndCheck(entry.getUser().getUniqueId(), entry.getAction(), 1);

    }

    /**
     * Used to handle a BlockPlaceEntry.
     *
     * @param input The entry.
     */

    public void handleBlockPlaceEntry(final Entry input) {

        final BlockPlaceEntry entry = (BlockPlaceEntry) input;
        final MaterialData materialData = entry.getNewBlockType().getMaterialData();

        if (!ISLAND_FARMING_MATERIALS.contains(materialData.getItemType())) return;

        adjustLevelAndCheck(entry.getUser().getUniqueId(), entry.getAction(), -1);

    }

    /**
     * Used to handle an IslandEntry.
     *
     * @param input The entry.
     */

    public void handleIslandEntry(final Entry input) {

        final IslandEntry entry = (IslandEntry) input;
        final UUID playerUuid = entry.getUser().getUniqueId();

        if (entry.getIslandAction() != IslandEntry.IslandAction.REPLACE) return;

        adjustLevelAndCheck(playerUuid, entry.getAction(), suspiciousPlayers.containsKey(playerUuid) ? Math.max(getPerformedActionsCount(playerUuid), 10) : 10);
        suspiciousPlayers.put(playerUuid, true);

    }

    /**
     * Simple class to track performed actions.
     */

    private static class PerformedAction {

        private final Action action;

        public PerformedAction(final Action action) {

            this.action = action;

        }

        public Action getAction() {

            return action;

        }

    }

}
