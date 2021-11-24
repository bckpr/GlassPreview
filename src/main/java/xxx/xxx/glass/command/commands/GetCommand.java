package xxx.xxx.glass.command.commands;

import xxx.xxx.glass.Glass;
import xxx.xxx.glass.command.CommandOptions;
import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.arguments.Argument;
import xxx.xxx.glass.command.arguments.CommandArgument;
import xxx.xxx.glass.command.arguments.validators.UUIDArgumentValidator;
import xxx.xxx.glass.command.context.CommandExecutionContext;
import xxx.xxx.glass.common.ServiceProvider;
import xxx.xxx.glass.data.entry.Entry;
import xxx.xxx.glass.database.DatabaseCommunicator;
import xxx.xxx.glass.database.DatabaseResponse;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Command to search through the database for an entry with a specific unique id.
 */

public class GetCommand extends Command {

    private final JavaPlugin plugin;
    private final DatabaseCommunicator databaseCommunicator;

    public GetCommand(final ServiceProvider serviceProvider) {

        super(
                new CommandOptions.Builder()
                        .name("glass")
                        .subCommands(new SubCommand("get", "Get sub command."))
                        .description("Get command")
                        .usage("<uuid>")
                        .minArgs(1)
                        .maxArgs(1)
                        .arguments(new CommandArgument("uuid", "Entry uuid.", UUIDArgumentValidator.getInstance()))
                        .build()
        );

        this.plugin = serviceProvider.getService(Glass.class);
        this.databaseCommunicator = serviceProvider.getService(DatabaseCommunicator.class);

    }

    /**
     * Used to execute the command which will search through the database
     * to find an entry with the specified unique id.
     *
     * @param commandType             The command type.
     * @param commandExecutionContext The execution context.
     * @param arguments               The provided arguments.
     */

    @Override
    public void execute(final CommandType commandType, final CommandExecutionContext commandExecutionContext, final Argument... arguments) {

        final UUID uuid = arguments[0].getAsUUID();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final DatabaseResponse databaseResponse = databaseCommunicator.findEntryByUniqueIdEverywhere(uuid);
            if (databaseResponse.getStatus() == DatabaseResponse.Status.FAILED) {
                commandExecutionContext.getCommunicationChannel().sendMessage("Query failed!");
                return;
            }

            if (!databaseResponse.hasEntries()) {
                commandExecutionContext.getCommunicationChannel().sendMessage("Entry not found!");
                return;
            }

            final Entry entry = databaseResponse.getEntry();
            commandExecutionContext.getCommunicationChannel().sendComplexMessage(entry.toComplexMessage(commandType));
        });

    }

}
