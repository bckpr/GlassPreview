package xxx.xxx.glass.command.channel;

import xxx.xxx.glass.command.message.ComplexMessage;
import xxx.xxx.glass.command.message.DiscordComplexMessage;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

/**
 * CommunicationChannel implementation for discord text channels.
 */

public class DiscordCommunicationChannel implements CommunicationChannel {

    private final Mono<MessageChannel> channelMono;

    public DiscordCommunicationChannel(final Mono<MessageChannel> channelMono) {

        this.channelMono = channelMono;

    }

    /**
     * Used to send a simple message to a discord
     * text channel.
     *
     * @param message The simple message.
     */

    @Override
    public void sendMessage(final String message) {

        channelMono.flatMap(messageChannel -> messageChannel.createMessage(message)).subscribe();

    }

    /**
     * Used to send a complex message to a discord
     * text channel.
     *
     * @param complexMessage The complex message.
     */

    @Override
    public void sendComplexMessage(final ComplexMessage complexMessage) {

        final DiscordComplexMessage discordComplexMessage = (DiscordComplexMessage) complexMessage;
        channelMono.flatMap(messageChannel -> messageChannel.getRestChannel().createMessage(discordComplexMessage.getMultipartRequest())).subscribe();

    }

    /**
     * Used to get the channel.
     *
     * @return The channel.
     */

    public Mono<MessageChannel> getChannelMono() {

        return channelMono;

    }

}
