package xxx.xxx.glass;

import xxx.xxx.glass.analyzing.actions.CountermeasureManager;
import xxx.xxx.glass.analyzing.actions.IgnoreCountermeasure;
import xxx.xxx.glass.analyzing.modules.IpChangeAnalyzerModule;
import xxx.xxx.glass.analyzing.modules.IslandFarmingAnalyzerModule;
import xxx.xxx.glass.command.channel.DiscordCommunicationChannel;
import xxx.xxx.glass.command.commands.*;
import xxx.xxx.glass.common.ServiceProvider;
import xxx.xxx.glass.common.SyncLazyCache;
import xxx.xxx.glass.common.TimeSpan;
import xxx.xxx.glass.data.EntryCacheQueue;
import xxx.xxx.glass.analyzing.EntryCacheQueueAnalyzer;
import xxx.xxx.glass.command.CommandRegistry;
import xxx.xxx.glass.command.converter.DiscordSlashCommandConverter;
import xxx.xxx.glass.command.handlers.CraftCommandHandler;
import xxx.xxx.glass.command.handlers.DiscordCommandHandler;
import xxx.xxx.glass.command.handlers.CommandHandler;
import xxx.xxx.glass.command.handlers.DiscordSlashCommandHandler;
import xxx.xxx.glass.data.parser.EntryParserRegister;
import xxx.xxx.glass.database.*;
import xxx.xxx.glass.discord.ButtonClickHandler;
import xxx.xxx.glass.discord.ButtonRegistry;
import xxx.xxx.glass.internal.HealthStatus;
import xxx.xxx.glass.listeners.ActiveInventoryCollection;
import xxx.xxx.glass.listeners.BlockListener;
import xxx.xxx.glass.listeners.InventoryListener;
import xxx.xxx.glass.listeners.PlayerListener;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractEvent;
import discord4j.core.event.domain.interaction.SlashCommandEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import reactor.core.publisher.Mono;
import xxx.xxx.glass.data.entry.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main class that contains the "entry point".
 */

public class Glass extends JavaPlugin {

    public final static int QUEUE_THREAD_COUNT = 5;
    public final static int MAX_QUEUE_THREAD_COUNT = 10;
    private final static String DISCORD_TOKEN = "xxx";

    private final AtomicBoolean work = new AtomicBoolean(true);
    private final ThreadGroup threadGroup = new ThreadGroup("queue-workers");

    private final PluginManager pluginManager = getServer().getPluginManager();
    private final DiscordClient discordClient = DiscordClient.create(DISCORD_TOKEN);
    private final GatewayDiscordClient gateway = Objects.requireNonNull(discordClient.login().block());
    private final CommandRegistry commandRegistry = new CommandRegistry();
    private final EntryCacheQueue queue = new EntryCacheQueue(new LinkedBlockingQueue<>(), new SyncLazyCache<>(TimeSpan.fromMinutes(15)));
    private final DatabaseResponseCache databaseResponseCache = new DatabaseResponseCache(TimeSpan.fromMinutes(10));
    private final ActiveInventoryCollection activeInventories = new ActiveInventoryCollection();
    private final EntryParserRegister entryParserRegister = new EntryParserRegister();
    private final CommandHandler commandHandler = new CommandHandler(commandRegistry);
    private final ButtonRegistry buttonRegistry = new ButtonRegistry();
    private final CountermeasureManager countermeasureManager = new CountermeasureManager(this);
    private final HealthStatus healthStatus = new HealthStatus(threadGroup, queue.getQueue()); // Deprecated

    private ConnectionPoolManager connectionPoolManager;
    private MongoDatabaseCommunicator databaseCommunicator;

    /**
     * Main entry, gets called when the plugin gets enabled.
     */

    @Override
    public void onEnable() {

        setupFiles();
        if (!setupDatabase()) {
            getLogger().warning("Failed to connect to database! Disabling plugin...");
            pluginManager.disablePlugin(this);
            return;
        }

        entryParserRegister.register(Action.DROP_ITEM, new DropItemEntry.Parser());
        entryParserRegister.register(Action.PICKUP_ITEM, new PickupItemEntry.Parser());
        entryParserRegister.register(Action.INVENTORY_TRANSACTION, new InventoryTransactionEntry.Parser());
        entryParserRegister.register(Action.CHAT_MESSAGE, new ChatMessageEntry.Parser());
        entryParserRegister.register(Action.BLOCK_BREAK, new BlockBreakEntry.Parser());
        entryParserRegister.register(Action.BLOCK_PLACE, new BlockPlaceEntry.Parser());
        entryParserRegister.register(Action.COMMAND, new CommandEntry.Parser());
        entryParserRegister.register(Action.JOIN, new JoinEntry.Parser());
        entryParserRegister.register(Action.ISLAND, new IslandEntry.Parser());

        countermeasureManager.registerCountermeasure(new IgnoreCountermeasure());

        final ServiceProvider serviceProvider = new ServiceProvider.Builder()
                .addService(DatabaseCommunicator.class, databaseCommunicator)
                .addService(DatabaseResponseCache.class, databaseResponseCache)
                .addService(ActiveInventoryCollection.class, activeInventories)
                .addService(DiscordClient.class, discordClient)
                .addService(CommandRegistry.class, commandRegistry)
                .addService(CommandHandler.class, commandHandler)
                .addService(EntryCacheQueue.class, queue)
                .addService(ButtonRegistry.class, buttonRegistry)
                .addService(CountermeasureManager.class, countermeasureManager)
                .addService(HealthStatus.class, healthStatus)
                .addService(Glass.class, this)
                .build();

        if (gateway == null) return;

        final DiscordCommunicationChannel discordCommunicationChannel = new DiscordCommunicationChannel(
                Mono.justOrEmpty(gateway.getChannelById(Snowflake.of(getConfig().getLong("notification-channel-id"))).map(channel -> ((MessageChannel) channel)).block())
        );

        queue.registerListener(new EntryCacheQueueAnalyzer(serviceProvider,
                new IpChangeAnalyzerModule(discordCommunicationChannel, countermeasureManager.getCountermeasure(IgnoreCountermeasure.class), serviceProvider),
                new IslandFarmingAnalyzerModule(discordCommunicationChannel, serviceProvider)
        ));

        registerListeners(serviceProvider);
        registerCommands(serviceProvider);

        getServer().getScheduler().runTaskLater(this, countermeasureManager::initialize, 1L);

        getLogger().info("Starting threads...");
        for (int i = 0; i < QUEUE_THREAD_COUNT; i++)
            startQueueWorker("queue-worker-" + i, threadGroup);

    }

    /**
     * Starts a queue process worker thread that'll process entries that
     * got added to the queue.
     *
     * @param name        Name of the thread.
     * @param threadGroup The thread group that the new thread should be in.
     */

    private void startQueueWorker(final String name, final ThreadGroup threadGroup) {

        getLogger().info(String.format("Starting thread '%s' (%s)...", name, threadGroup.getName()));
        new Thread(threadGroup, () -> {
            getLogger().info(String.format("Thread '%s' (%s) started!", name, threadGroup.getName()));
            while (work.get()) {
                try {
                    final Entry entry = queue.take();
                    if (entry == null) continue;
                    databaseCommunicator.insertEntry(entry);
                } catch (final Exception ex) {
                    getLogger().warning("(" + Thread.currentThread() + getName() + ") " + ex.getMessage());
                }
            }
            getLogger().info(String.format("Thread '%s' (%s) is stopping!", name, threadGroup.getName()));
        }, name).start();

    }

    /**
     * Saves the default config file resource.
     */

    private void setupFiles() {

        saveDefaultConfig();

    }

    /**
     * Sets up the database communicator, currently only supports MongoDB
     * because the (My)SQL implementation is unfinished.
     *
     * @return <code>true</code> if the setup was successful.
     */

    private boolean setupDatabase() {

        databaseCommunicator = new MongoDatabaseCommunicator(entryParserRegister, databaseResponseCache);
        databaseCommunicator.connect(getConfig().getString("host"), getConfig().getInt("port"), getConfig().getString("database"));
        return true;

    }

    /**
     * Registers all listeners.
     *
     * @param serviceProvider The service provider instance that provides access to the dependencies.
     */

    private void registerListeners(final ServiceProvider serviceProvider) {

        pluginManager.registerEvents(new BlockListener(queue), this);
        pluginManager.registerEvents(new InventoryListener(queue, serviceProvider), this);
        pluginManager.registerEvents(new PlayerListener(queue), this);

        gateway.on(MessageCreateEvent.class).subscribe(new DiscordCommandHandler(serviceProvider));
        gateway.on(SlashCommandEvent.class).subscribe(new DiscordSlashCommandHandler(serviceProvider));
        gateway.on(ButtonInteractEvent.class).subscribe(new ButtonClickHandler(serviceProvider));

    }

    /**
     * Registers all commands.
     *
     * @param serviceProvider The service provider instance that provides access to the dependencies.
     */

    private void registerCommands(final ServiceProvider serviceProvider) {

        final CraftCommandHandler craftCommandHandler = new CraftCommandHandler(serviceProvider);
        for (final String command : getDescription().getCommands().keySet())
            Objects.requireNonNull(getCommand(command)).setExecutor(craftCommandHandler);

        commandRegistry.registerCommand(new CountermeasuresClearCommand(serviceProvider));
        commandRegistry.registerCommand(new FilterCommand(serviceProvider));
        commandRegistry.registerCommand(new GetCommand(serviceProvider));
        commandRegistry.registerCommand(new HelpCommand(serviceProvider));
        commandRegistry.registerCommand(new StatusCommand(serviceProvider));

        final Set<String> knownCommands = new HashSet<>();
        final List<ApplicationCommandRequest> requests = new ArrayList<>();
        for (final Command registeredCommand : commandRegistry.getRegisteredCommands()) {
            if (knownCommands.contains(registeredCommand.getCommandOptions().getName().toLowerCase())) continue;
            requests.add(DiscordSlashCommandConverter.getInstance().convert(commandRegistry, registeredCommand));
            knownCommands.add(registeredCommand.getCommandOptions().getName().toLowerCase());
        }

        // Process requests

    }

    /**
     * Stops all systems, gets called when the plugin gets unloaded.
     */

    @Override
    public void onDisable() {

        gateway.logout();
        databaseCommunicator.close();
        work.set(false);
        threadGroup.interrupt();

    }

}
