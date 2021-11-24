package xxx.xxx.glass.command.handlers;

import xxx.xxx.glass.command.context.DiscordSlashCommandExecutionContext;
import xxx.xxx.glass.common.ServiceProvider;
import discord4j.core.event.domain.interaction.SlashCommandEvent;

import java.util.function.Consumer;

/**
 * Command handler for discord slash commands.
 */

public class DiscordSlashCommandHandler implements Consumer<SlashCommandEvent> {

    private final CommandHandler commandHandler;

    public DiscordSlashCommandHandler(final ServiceProvider serviceProvider) {

        this.commandHandler = serviceProvider.getService(CommandHandler.class);

    }

    /**
     * Gets called when a discord user executes a slash command, used to
     * forward it to the CommandHandler which then processes it.
     *
     * @param event The triggered event instance.
     */

    @Override
    public void accept(final SlashCommandEvent event) {

        commandHandler.onCommand(new DiscordSlashCommandExecutionContext(event));

    }

}
