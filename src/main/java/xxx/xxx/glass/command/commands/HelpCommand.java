package xxx.xxx.glass.command.commands;

import xxx.xxx.glass.command.CommandOptions;
import xxx.xxx.glass.command.CommandRegistry;
import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.arguments.Argument;
import xxx.xxx.glass.command.context.CommandExecutionContext;
import xxx.xxx.glass.command.message.ComplexMessage;
import xxx.xxx.glass.command.message.CraftComplexMessage;
import xxx.xxx.glass.command.message.DiscordComplexMessage;
import xxx.xxx.glass.command.message.DiscordSlashComplexMessage;
import xxx.xxx.glass.common.ServiceProvider;
import xxx.xxx.glass.exceptions.NotImplementedException;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Command to display available commands and how to use them.
 */

public class HelpCommand extends Command {

    private final CommandRegistry commandRegistry;

    public HelpCommand(final ServiceProvider serviceProvider) {

        super(
                new CommandOptions.Builder()
                        .name("glass")
                        .subCommands(new SubCommand("help", "Help sub command."))
                        .description("Help command")
                        .build()
        );

        this.commandRegistry = serviceProvider.getService(CommandRegistry.class);

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

        commandExecutionContext.getCommunicationChannel().sendComplexMessage(generateComplexHelpMessage(commandType));

    }

    /**
     * Used to create a complex help message based on the provided command type.
     *
     * @param commandType The command type.
     * @return The created complex help message.
     */

    @NotNull
    private ComplexMessage generateComplexHelpMessage(final CommandType commandType) {

        final List<Command> registeredCommands = commandRegistry.getRegisteredCommands();

        ComplexMessage complexMessage;
        switch (commandType) {
            case CRAFT:
                final ComponentBuilder componentBuilder =
                        new ComponentBuilder("Registered commands:\n").color(ChatColor.GOLD).bold(true)
                                .append("< > = Required, ( ) = Optional\n").reset().color(ChatColor.GRAY).italic(true);
                for (final Command command : registeredCommands) {
                    componentBuilder
                            .append("\n")
                            .append("- ").reset().color(ChatColor.GRAY)
                            .append("/" + command.toString()).reset().color(ChatColor.YELLOW);
                }

                complexMessage = new CraftComplexMessage(componentBuilder.create());
                break;
            case DISCORD:
                complexMessage = new DiscordComplexMessage(
                        new MessageCreateSpec().addEmbed(embed -> {
                            embed.setTitle("Registered commands");
                            embed.setDescription("< > = Required, ( ) = Optional");
                            embed.setColor(Color.of(9, 132, 227));
                            for (final Command command : registeredCommands)
                                embed.addField("." + command.getCommandOptions().getName() + " " + Arrays.stream(command.getCommandOptions().getSubCommands()).map(SubCommand::getName).collect(Collectors.joining(" ")), "." + command.toString(), false);
                        }).asRequest()
                );
                break;
            case DISCORD_SLASH:
                final Consumer<InteractionApplicationCommandCallbackSpec> specConsumer = interaction -> {
                    interaction.setEphemeral(true);
                    interaction.addEmbed(embed -> {
                        embed.setTitle("Registered commands");
                        embed.setDescription("< > = Required, ( ) = Optional");
                        embed.setColor(Color.of(9, 132, 227));
                        for (final Command command : registeredCommands)
                            embed.addField("/" + command.getCommandOptions().getName() + " " + Arrays.stream(command.getCommandOptions().getSubCommands()).map(SubCommand::getName).collect(Collectors.joining(" ")), "." + command.toString(), false);
                    });
                };

                complexMessage = new DiscordSlashComplexMessage(specConsumer);
                break;
            default:
                throw new NotImplementedException();
        }

        return complexMessage;

    }

}
