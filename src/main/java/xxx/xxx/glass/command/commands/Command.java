package xxx.xxx.glass.command.commands;

import xxx.xxx.glass.command.CommandOptions;
import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.arguments.Argument;
import xxx.xxx.glass.command.arguments.CommandArgument;
import xxx.xxx.glass.command.context.CommandExecutionContext;
import xxx.xxx.glass.utils.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Abstract command class
 */

public abstract class Command {

    private final CommandOptions commandOptions;

    public Command(final CommandOptions commandOptions) {

        this.commandOptions = commandOptions;

    }

    public CommandOptions getCommandOptions() {

        return commandOptions;

    }

    /**
     * Converts the command to a readable string.
     *
     * @return The readable string.
     */

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();
        if (commandOptions.getArguments().length > 0 && commandOptions.getUsage().length() == 0) {
            final CommandArgument[] arguments = commandOptions.getArguments();
            for (int i = 0; i < arguments.length; i++) {
                if (builder.length() != 0) builder.append(" ");
                final CommandArgument argument = arguments[i];
                final boolean optional = i + 1 > commandOptions.getMinArgs();
                builder.append(StringUtils.surround(argument.getName(), optional ? "(" : "<", optional ? ")" : ">"));
            }
        } else {
            builder.append(commandOptions.getUsage());
        }

        return String.format("%s %s %s", commandOptions.getName(), Arrays.stream(commandOptions.getSubCommands()).map(SubCommand::getName).collect(Collectors.joining(" ")), builder);

    }

    public abstract void execute(final CommandType commandType, final CommandExecutionContext commandExecutionContext, final Argument... arguments);

}