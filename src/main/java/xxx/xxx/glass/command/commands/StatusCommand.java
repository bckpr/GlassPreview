package xxx.xxx.glass.command.commands;

import xxx.xxx.glass.command.CommandOptions;
import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.arguments.Argument;
import xxx.xxx.glass.command.context.CommandExecutionContext;
import xxx.xxx.glass.command.message.ComplexMessage;
import xxx.xxx.glass.command.message.CraftComplexMessage;
import xxx.xxx.glass.command.message.DiscordComplexMessage;
import xxx.xxx.glass.command.message.DiscordSlashComplexMessage;
import xxx.xxx.glass.common.ServiceProvider;
import xxx.xxx.glass.exceptions.NotImplementedException;
import xxx.xxx.glass.internal.HealthStatus;
import xxx.xxx.glass.utils.ColorUtils;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.function.Consumer;

/**
 * Command to check the status of the plugin.
 */

public class StatusCommand extends Command {

    private final HealthStatus healthStatus;

    public StatusCommand(final ServiceProvider serviceProvider) {

        super(
                new CommandOptions.Builder()
                        .name("glass")
                        .subCommands(new SubCommand("status", "Displays the current status."))
                        .description("Status command")
                        .build()
        );

        healthStatus = serviceProvider.getService(HealthStatus.class);

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

        commandExecutionContext.getCommunicationChannel().sendComplexMessage(generateComplexStatusMessage(commandType));

    }

    /**
     * Used to create a complex status message based on the provided command type.
     *
     * @param commandType The command type.
     * @return The created complex status message.
     */

    private ComplexMessage generateComplexStatusMessage(final CommandType commandType) {

        ComplexMessage complexMessage;
        switch (commandType) {
            case CRAFT:
                complexMessage = new CraftComplexMessage(
                        new ComponentBuilder("Current Status:")
                                .append("\nActive Threads: " + healthStatus.getThreadGroup().activeCount())
                                .append("\nQueue Size: " + healthStatus.getQueueSize())
                                .create()
                );
                break;
            case DISCORD:
                complexMessage = new DiscordComplexMessage(new MessageCreateSpec().addEmbed(generateEmbed()).asRequest());
                break;
            case DISCORD_SLASH:
                complexMessage = new DiscordSlashComplexMessage(spec -> {
                    spec.setEphemeral(true);
                    spec.addEmbed(generateEmbed());
                });
                break;
            default:
                throw new NotImplementedException();
        }

        return complexMessage;

    }

    /**
     * Used to create an embed spec for complex discord messages.
     *
     * @return The created embed spec.
     */

    private Consumer<EmbedCreateSpec> generateEmbed() {

        return (embed) -> {
            embed.setTitle("Current Status");
            embed.setDescription("The current status of glass's internal systems.");
            embed.setColor(ColorUtils.convertColor(ColorUtils.getColorByLevel(healthStatus.calculateHealthLevel())));
            embed.addField("Active Threads", Integer.toString(healthStatus.getThreadGroup().activeCount()), true);
            embed.addField("Queue Size", Integer.toString(healthStatus.getQueueSize()), true);
        };

    }

}
