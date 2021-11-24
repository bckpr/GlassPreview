package xxx.xxx.glass.command.channel;

import xxx.xxx.glass.command.message.ComplexMessage;
import xxx.xxx.glass.command.message.DiscordSlashComplexMessage;
import discord4j.core.event.domain.interaction.SlashCommandEvent;

/**
 * CommunicationChannel implementation for discord slash commands.
 */

public class DiscordSlashCommunicationChannel implements CommunicationChannel {

    private final SlashCommandEvent slashCommandEvent;

    public DiscordSlashCommunicationChannel(final SlashCommandEvent slashCommandEvent) {

        this.slashCommandEvent = slashCommandEvent;

    }

    /**
     * Used to send a simple message response to
     * an executed discord command.
     *
     * @param message The simple message.
     */

    @Override
    public void sendMessage(final String message) {

        slashCommandEvent.replyEphemeral(message).subscribe();

    }

    /**
     * Used to send a complex message response to
     * an executed discord command.
     *
     * @param complexMessage The complex message.
     */

    @Override
    public void sendComplexMessage(final ComplexMessage complexMessage) {

        final DiscordSlashComplexMessage discordSlashComplexMessage = (DiscordSlashComplexMessage) complexMessage;
        slashCommandEvent.reply(discordSlashComplexMessage.getSpecConsumer()).subscribe();

    }

}
