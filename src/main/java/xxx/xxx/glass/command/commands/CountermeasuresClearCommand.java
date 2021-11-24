package xxx.xxx.glass.command.commands;

import xxx.xxx.glass.analyzing.actions.Countermeasure;
import xxx.xxx.glass.analyzing.actions.CountermeasureManager;
import xxx.xxx.glass.command.CommandOptions;
import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.arguments.Argument;
import xxx.xxx.glass.command.arguments.CommandArgument;
import xxx.xxx.glass.command.arguments.validators.StringArgumentValidator;
import xxx.xxx.glass.command.context.CommandExecutionContext;
import xxx.xxx.glass.common.ServiceProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Command used to clear the active countermeasures that affect the specified player.
 */

public class CountermeasuresClearCommand extends Command {

    private final CountermeasureManager countermeasureManager;

    public CountermeasuresClearCommand(final ServiceProvider serviceProvider) {

        super(
                new CommandOptions.Builder()
                        .name("glass")
                        .subCommands(
                                new SubCommand("countermeasures", "Countermeasures sub command group."),
                                new SubCommand("clear", "Clear active countermeasures affecting a player.")
                        )
                        .description("Countermeasures command.")
                        .usage("<username>")
                        .minArgs(1)
                        .maxArgs(1)
                        .arguments(new CommandArgument("username", "The username of the player you want to target.", StringArgumentValidator.getInstance()))
                        .build()
        );

        this.countermeasureManager = serviceProvider.getService(CountermeasureManager.class);

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

        final Player player = Bukkit.getPlayer(arguments[0].getAsString());
        if (player == null) {
            commandExecutionContext.getCommunicationChannel().sendMessage("Player not found!");
            return;
        }

        for (final Countermeasure registeredCountermeasure : countermeasureManager.getRegisteredCountermeasures())
            registeredCountermeasure.cancel(player);

        commandExecutionContext.getCommunicationChannel().sendMessage("Cancelled all countermeasures affecting " + player.getName() + ".");

    }

}
