package xxx.xxx.glass.command.handlers;

import xxx.xxx.glass.command.context.CraftCommandExecutionContext;
import xxx.xxx.glass.common.ServiceProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command handler for craft commands.
 */

public class CraftCommandHandler implements CommandExecutor {

    private final CommandHandler commandHandler;

    public CraftCommandHandler(final ServiceProvider serviceProvider) {

        this.commandHandler = serviceProvider.getService(CommandHandler.class);

    }

    /**
     * Gets called when a craft player executed a command, used to forward it
     * to the CommandHandler which then processes it.
     *
     * @param sender  The command sender.
     * @param cmd     The command.
     * @param cmdName The command name.
     * @param args    The string arguments.
     * @return <code>true</code> after forwarding the command.
     */

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String cmdName, final String[] args) {

        commandHandler.onCommand(new CraftCommandExecutionContext(sender, cmd, cmdName, args));
        return true;

    }

}
