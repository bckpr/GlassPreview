package xxx.xxx.glass.command.context;

import xxx.xxx.glass.command.channel.DiscordCommunicationChannel;
import xxx.xxx.glass.command.channel.CommunicationChannel;
import discord4j.core.event.domain.message.MessageCreateEvent;

/**
 * Execution context for discord text commands.
 */

public class DiscordCommandExecutionContext implements CommandExecutionContext {

    private final MessageCreateEvent messageCreateEvent;

    public DiscordCommandExecutionContext(final MessageCreateEvent messageCreateEvent) {

        this.messageCreateEvent = messageCreateEvent;

    }

    /**
     * Used to get the CommunicationChannel for discord text channels.
     *
     * @return The discord text CommunicationChannel.
     */

    @Override
    public CommunicationChannel getCommunicationChannel() {

        return new DiscordCommunicationChannel(messageCreateEvent.getMessage().getChannel());

    }

    /**
     * Used to get the original creation event.
     *
     * @return The original creation event.
     */

    public MessageCreateEvent getMessageCreateEvent() {

        return messageCreateEvent;

    }

}
