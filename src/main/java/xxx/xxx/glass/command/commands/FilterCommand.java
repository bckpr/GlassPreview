package xxx.xxx.glass.command.commands;

import xxx.xxx.glass.Glass;
import xxx.xxx.glass.command.CommandOptions;
import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.arguments.Argument;
import xxx.xxx.glass.command.arguments.CommandArgument;
import xxx.xxx.glass.command.arguments.validators.StringArgumentValidator;
import xxx.xxx.glass.command.context.CommandExecutionContext;
import xxx.xxx.glass.common.ServiceProvider;
import xxx.xxx.glass.data.entry.Entry;
import xxx.xxx.glass.database.DatabaseCommunicator;
import xxx.xxx.glass.database.DatabaseResponse;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to search through the database using a query.
 */

public class FilterCommand extends Command {

    private final JavaPlugin plugin;
    private final DatabaseCommunicator databaseCommunicator;

    public FilterCommand(final ServiceProvider serviceProvider) {

        super(
                new CommandOptions.Builder()
                        .name("glass")
                        .subCommands(new SubCommand("filter", "Filter sub command."))
                        .description("Filter command")
                        .usage("<action> <parameter> (parameter)...")
                        .minArgs(2)
                        .maxArgs(25)
                        .arguments(
                                new CommandArgument("action", "The action you want to filter for.", StringArgumentValidator.getInstance()),
                                new CommandArgument("parameter", "A parameter for the database query.", StringArgumentValidator.getInstance())
                        )
                        .build()
        );

        this.plugin = serviceProvider.getService(Glass.class);
        this.databaseCommunicator = serviceProvider.getService(DatabaseCommunicator.class);

    }

    /**
     * Used to execute the command.
     *
     * @param commandType             The command type.
     * @param commandExecutionContext The execution context.
     * @param arguments               The provided arguments.
     */

    @Override
    public void execute(final CommandType commandType, final CommandExecutionContext commandExecutionContext, final Argument... arguments) {

        final String databaseLocation = arguments[0].getAsString();
        final String query = Arrays.stream(arguments).map(Argument::getAsString).collect(Collectors.joining(" "));
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final DatabaseResponse databaseResponse = databaseCommunicator.findEntriesByQuery(databaseLocation, query);
            if (databaseResponse.getStatus() == DatabaseResponse.Status.FAILED) {
                commandExecutionContext.getCommunicationChannel().sendMessage("Query failed!");
                return;
            }

            if (!databaseResponse.hasEntries()) {
                commandExecutionContext.getCommunicationChannel().sendMessage("Found no entries!");
                return;
            }

            final List<Entry> entries = databaseResponse.getEntries();
            final StringBuilder builder = new StringBuilder();
            for (final Entry entry : entries) {
                if (builder.length() != 0) builder.append("\n");
                builder.append("UUID: ").append(entry.getUniqueId().toString());
            }

            commandExecutionContext.getCommunicationChannel().sendMessage(builder.toString());
        });

    }

}
