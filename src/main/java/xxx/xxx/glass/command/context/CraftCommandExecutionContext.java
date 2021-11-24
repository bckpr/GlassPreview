package xxx.xxx.glass.command.context;

import xxx.xxx.glass.command.channel.CraftCommunicationChannel;
import xxx.xxx.glass.command.channel.CommunicationChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Execution context for craft server implementation commands.
 */

public class CraftCommandExecutionContext implements CommandExecutionContext {

    private final CommandSender sender;
    private final Command command;
    private final String commandName;
    private final String[] arguments;

    public CraftCommandExecutionContext(final CommandSender sender,
                                        final Command command,
                                        final String commandName,
                                        final String[] arguments) {

        this.sender = sender;
        this.command = command;
        this.commandName = commandName;
        this.arguments = arguments;

    }

    /**
     * Used to get the CommunicationChannel for craft implementations.
     *
     * @return The craft CommunicationChannel.
     */

    @Override
    public CommunicationChannel getCommunicationChannel() {

        return new CraftCommunicationChannel(sender);

    }

    /**
     * Used to get the sender of the command.
     *
     * @return The sender.
     */

    public CommandSender getSender() {

        return sender;

    }

    /**
     * Used to get the command.
     *
     * @return The command.
     */

    public Command getCommand() {

        return command;

    }

    /**
     * Used to get the command name.
     *
     * @return The command name.
     */

    public String getCommandName() {

        return commandName;

    }

    /**
     * Used to get the command string arguments.
     *
     * @return The string arguments.
     */

    public String[] getArguments() {

        return arguments;

    }

}
