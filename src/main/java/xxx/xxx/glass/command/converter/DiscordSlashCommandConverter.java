package xxx.xxx.glass.command.converter;

import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.ApplicationCommandOptionType;
import xxx.xxx.glass.command.CommandOptions;
import xxx.xxx.glass.command.CommandRegistry;
import xxx.xxx.glass.command.arguments.CommandArgument;
import xxx.xxx.glass.command.commands.Command;
import xxx.xxx.glass.command.commands.SubCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to convert command options to a discord command usable state.
 */

public class DiscordSlashCommandConverter implements CommandConverter<ApplicationCommandRequest> {

    /**
     * Safe singleton implementation.
     */

    private static class DiscordCommandConverterSingleton {

        private final static DiscordSlashCommandConverter INSTANCE = new DiscordSlashCommandConverter();

    }

    private DiscordSlashCommandConverter() {

    }

    /**
     * Used to convert a command to a discord compatible command request for registering.
     *
     * @param commandRegistry The command registry.
     * @param command         The command.
     * @return The converted request.
     */

    @Override
    public ApplicationCommandRequest convert(final CommandRegistry commandRegistry, final Command command) {

        final List<Command> commands = commandRegistry.getRegisteredCommands()
                .stream()
                .filter(cmd -> cmd.getCommandOptions().getName().equalsIgnoreCase(command.getCommandOptions().getName()))
                .collect(Collectors.toList());

        if (commands.size() == 0) return null;

        final String name = commands.get(0).getCommandOptions().getName();
        final String description = commands.stream()
                .map(Command::getCommandOptions)
                .filter(commandOptions -> commandOptions.getSubCommands().length == 0)
                .map(CommandOptions::getDescription).findFirst().orElse(name);

        return ApplicationCommandRequest.builder()
                .name(name)
                .description(description)
                .addAllOptions(generateOptions(commands))
                .build();

    }

    /**
     * Converts the options of the provided list to discord usable options.
     *
     * @param commands The input commands.
     * @return The converted option list.
     */

    private List<ApplicationCommandOptionData> generateOptions(final List<Command> commands) {

        final List<ApplicationCommandOptionData> options = new ArrayList<>();
        for (final Command command : commands) {
            final CommandOptions commandOptions = command.getCommandOptions();
            if (commandOptions.getSubCommands().length == 0) continue;
            final int subCommandCount = commandOptions.getSubCommands().length;
            ApplicationCommandOptionData optionData = null;
            for (int i = 0; i < subCommandCount; i++) {
                final SubCommand subCommand = commandOptions.getSubCommands()[i];
                final int type = i < subCommandCount - 1 ? ApplicationCommandOptionType.SUB_COMMAND_GROUP.getValue() : ApplicationCommandOptionType.SUB_COMMAND.getValue();
                final boolean withArguments = commandOptions.getArguments().length > 0 && i == subCommandCount - 1;
                if (optionData == null) {
                    optionData = generateSubCommandOptionData(command, subCommand, type, withArguments);
                } else {
                    optionData = ApplicationCommandOptionData.builder()
                            .from(optionData)
                            .addOption(generateSubCommandOptionData(command, subCommand, type, withArguments))
                            .build();
                }
            }

            options.add(optionData);
        }

        return options;

    }

    /**
     * Used to convert the provided sub command to a discord usable version.
     *
     * @param command       The command.
     * @param subCommand    The sub command.
     * @param type          The discord command option type.
     * @param withArguments If arguments should be added or not.
     * @return The converted sub command.
     */

    private ApplicationCommandOptionData generateSubCommandOptionData(final Command command, final SubCommand subCommand, final int type, boolean withArguments) {

        final ApplicationCommandOptionData optionData = ApplicationCommandOptionData.builder()
                .name(subCommand.getName())
                .description(subCommand.getDescription())
                .type(type)
                .build();

        if (withArguments) {
            return ApplicationCommandOptionData.builder()
                    .from(optionData)
                    .addAllOptions(generateArgumentsCommandOptionData(command))
                    .build();
        } else {
            return optionData;
        }

    }

    private List<ApplicationCommandOptionData> generateArgumentsCommandOptionData(final Command command) {

        final CommandArgument[] commandArguments = command.getCommandOptions().getArguments();
        final List<ApplicationCommandOptionData> argumentsOptionData = new ArrayList<>(command.getCommandOptions().getMaxArgs());
        for (int i = 0; i < command.getCommandOptions().getMaxArgs(); i++) {
            CommandArgument argument;
            boolean overflow = false;
            if (i >= commandArguments.length) {
                argument = commandArguments[commandArguments.length - 1];
                overflow = true;
            } else {
                argument = commandArguments[i];
            }

            final boolean required = i + 1 <= command.getCommandOptions().getMinArgs();
            argumentsOptionData.add(
                    ApplicationCommandOptionData.builder()
                            .name(argument.getName() + (overflow ? +i : ""))
                            .description(argument.getDescription() + (overflow ? " #" + i : ""))
                            .type(getTypeSafe(argument))
                            .required(required)
                            .build()
            );
        }

        return argumentsOptionData;

    }

    /**
     * Used to get the type id of the provided command argument.
     *
     * @param commandArgument The command argument.
     * @return The type id.
     */

    private int getTypeSafe(final CommandArgument commandArgument) {

        try {
            final ApplicationCommandOptionType type = ApplicationCommandOptionType.valueOf(commandArgument.getValidator().getUnderlyingClass().getSimpleName().toUpperCase());
            return type.getValue();
        } catch (final IllegalArgumentException ex) {
            return ApplicationCommandOptionType.STRING.getValue();
        }

    }

    /**
     * Used to get the singleton instance.
     *
     * @return The singleton instance.
     */

    public static DiscordSlashCommandConverter getInstance() {

        return DiscordCommandConverterSingleton.INSTANCE;

    }

}
