package xxx.xxx.glass.command.handlers;

import xxx.xxx.glass.command.CommandOptions;
import xxx.xxx.glass.command.CommandRegistry;
import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.arguments.Argument;
import xxx.xxx.glass.command.arguments.CommandArgument;
import xxx.xxx.glass.command.arguments.StringArgument;
import xxx.xxx.glass.command.arguments.validators.ArgumentValidator;
import xxx.xxx.glass.command.commands.Command;
import xxx.xxx.glass.command.commands.SubCommand;
import xxx.xxx.glass.command.context.CommandExecutionContext;
import xxx.xxx.glass.command.context.CraftCommandExecutionContext;
import xxx.xxx.glass.command.context.DiscordCommandExecutionContext;
import xxx.xxx.glass.command.context.DiscordSlashCommandExecutionContext;
import discord4j.core.object.command.ApplicationCommandInteraction;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Command handler that handles the execution of all command types. Might require refactoring in the future.
 */

public class CommandHandler {

    private final Map<Class<? extends CommandExecutionContext>, Consumer<CommandExecutionContext>> handlers = new HashMap<>();

    {
        handlers.put(CraftCommandExecutionContext.class, this::handleCraftCommand);
        handlers.put(DiscordCommandExecutionContext.class, this::handleDiscordCommand);
        handlers.put(DiscordSlashCommandExecutionContext.class, this::handleDiscordSlashCommand);
    }

    private final CommandRegistry commandRegistry;

    public CommandHandler(final CommandRegistry commandRegistry) {

        this.commandRegistry = commandRegistry;

    }

    /**
     * Forwards the CommandExecutionContext to the proper handler.
     *
     * @param context The execution context.
     */

    public void onCommand(final CommandExecutionContext context) {

        handlers.get(context.getClass()).accept(context);

    }

    /**
     * Handles craft commands.
     *
     * @param commandExecutionContext The execution context.
     */

    private void handleCraftCommand(final CommandExecutionContext commandExecutionContext) {

        final CraftCommandExecutionContext context = (CraftCommandExecutionContext) commandExecutionContext;
        processUserInput(CommandType.CRAFT, context, context.getCommandName(), context.getArguments());

    }

    /**
     * Handles discord commands.
     *
     * @param commandExecutionContext The execution context.
     */

    private void handleDiscordCommand(final CommandExecutionContext commandExecutionContext) {

        final DiscordCommandExecutionContext context = (DiscordCommandExecutionContext) commandExecutionContext;

        String messageContent = context.getMessageCreateEvent().getMessage().getContent();
        if (!messageContent.startsWith(".")) return;
        messageContent = messageContent.substring(1);

        final String[] messageParts = messageContent.contains(" ") ? messageContent.split(" ") : new String[0];
        final String commandName = messageParts.length > 1 ? messageParts[0] : messageContent;
        final String[] arguments = Arrays.copyOfRange(messageParts, 1, messageParts.length);

        processUserInput(CommandType.DISCORD, context, commandName, arguments);

    }

    /**
     * Handles discord slash commands.
     *
     * @param commandExecutionContext The execution context.
     */

    private void handleDiscordSlashCommand(final CommandExecutionContext commandExecutionContext) {

        final DiscordSlashCommandExecutionContext context = (DiscordSlashCommandExecutionContext) commandExecutionContext;

        final String commandName = context.getSlashCommandEvent().getCommandName();
        final LinkedList<String> providedArguments = new LinkedList<>();
        final ApplicationCommandInteraction commandInteraction = context.getSlashCommandEvent().getInteraction().getCommandInteraction().orElse(null);
        if (commandInteraction != null) {
            for (ApplicationCommandInteractionOption option : commandInteraction.getOptions())
                processInteractionOption(providedArguments, option);
        }

        final String[] arguments = new String[providedArguments.size()];
        for (int i = 0; i < providedArguments.size(); i++)
            arguments[i] = providedArguments.get(i);

        processUserInput(CommandType.DISCORD_SLASH, context, commandName, arguments);

    }

    /**
     * Internal method to process slash command interaction options.
     *
     * @param providedArguments The provided arguments.
     * @param option            The interaction options.
     */

    private void processInteractionOption(final LinkedList<String> providedArguments, ApplicationCommandInteractionOption option) {

        providedArguments.addLast(option.getValue().isPresent() ? option.getValue().get().asString() : option.getName());
        if (!option.getOptions().isEmpty()) {
            for (final ApplicationCommandInteractionOption subOption : option.getOptions())
                processInteractionOption(providedArguments, subOption);
        }

    }

    /**
     * Internal method to process the extracted user input and forward it
     * to the actual command implementation.
     *
     * @param commandType The command type.
     * @param context     The execution context.
     * @param commandName The command name.
     * @param arguments   The string arguments.
     */

    private void processUserInput(final CommandType commandType, final CommandExecutionContext context, final String commandName, final String[] arguments) {

        final Command command = findCommand(commandName, arguments);
        if (command == null) return;

        final int subNameCount = command.getCommandOptions().getSubCommands().length;
        final Argument[] args = new Argument[arguments.length - subNameCount];
        for (int i = 0; i < args.length; i++)
            args[i] = new StringArgument(arguments[i + subNameCount]);

        command.execute(commandType, context, args);

    }

    /**
     * Internal method to find a command implementation in the registry
     * based on the command name and string arguments.
     *
     * @param commandName The command name.
     * @param arguments   The string arguments.
     * @return The found command or <code>null</code> if no command was found.
     */

    @Nullable
    private Command findCommand(final String commandName, final String[] arguments) {

        for (final Command command : commandRegistry.getRegisteredCommands()) {
            final CommandOptions commandOptions = command.getCommandOptions();
            if (!commandOptions.getName().equalsIgnoreCase(commandName)) continue;
            if (arguments.length < (commandOptions.getMinArgs() + commandOptions.getSubCommands().length) ||
                    arguments.length > commandOptions.getMaxArgs() + commandOptions.getSubCommands().length) continue;

            boolean validCandidate = true;
            final SubCommand[] subCommands = commandOptions.getSubCommands();
            for (int i = 0; i < subCommands.length; i++) {
                if (!subCommands[i].getName().equalsIgnoreCase(arguments[i])) {
                    // INVALID SUB NAME
                    validCandidate = false;
                    break;
                }
            }

            if (!validCandidate) continue;

            final String[] filteredArgs = Arrays.copyOfRange(arguments, subCommands.length, arguments.length);
            final CommandArgument[] commandArguments = commandOptions.getArguments();
            for (int i = 0; i < filteredArgs.length; i++) {
                ArgumentValidator argumentValidator;
                if (i >= commandArguments.length) {
                    argumentValidator = commandArguments[commandArguments.length - 1].getValidator();
                } else {
                    argumentValidator = commandArguments[i].getValidator();
                }

                if (!argumentValidator.isValid(filteredArgs[i])) {
                    // INVALID ARG TYPE
                    validCandidate = false;
                    break;
                }
            }

            if (validCandidate) return command;
        }

        return null;

    }

}
