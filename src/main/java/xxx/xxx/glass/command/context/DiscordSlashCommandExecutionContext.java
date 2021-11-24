package xxx.xxx.glass.command.context;

import xxx.xxx.glass.command.channel.CommunicationChannel;
import xxx.xxx.glass.command.channel.DiscordSlashCommunicationChannel;
import discord4j.core.event.domain.interaction.SlashCommandEvent;

/**
 * Execution context for discord slash commands.
 */

public class DiscordSlashCommandExecutionContext implements CommandExecutionContext {

    private final SlashCommandEvent slashCommandEvent;

    public DiscordSlashCommandExecutionContext(final SlashCommandEvent slashCommandEvent) {

        this.slashCommandEvent = slashCommandEvent;

    }

    /**
     * Used to get the CommunicationChannel for discord slash commands.
     *
     * @return The discord slash CommunicationChannel.
     */

    @Override
    public CommunicationChannel getCommunicationChannel() {

        return new DiscordSlashCommunicationChannel(slashCommandEvent);

    }

    /**
     * Used to get the original slash command event.
     *
     * @return The original slash command event.
     */

    public SlashCommandEvent getSlashCommandEvent() {

        return slashCommandEvent;

    }

}
