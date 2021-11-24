package xxx.xxx.glass.command.handlers;

import xxx.xxx.glass.command.context.DiscordCommandExecutionContext;
import xxx.xxx.glass.common.ServiceProvider;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.function.Consumer;

/**
 * Command handler for discord chat commands.
 */

public class DiscordCommandHandler implements Consumer<MessageCreateEvent> {

    private final CommandHandler commandHandler;

    public DiscordCommandHandler(final ServiceProvider serviceProvider) {

        this.commandHandler = serviceProvider.getService(CommandHandler.class);

    }

    /**
     * Gets called when a discord user sends a message, used to forward
     * it to the CommandHandler which then processes it.
     *
     * @param event The triggered event instance.
     */

    @Override
    public void accept(final MessageCreateEvent event) {

        commandHandler.onCommand(new DiscordCommandExecutionContext(event));

    }

}
