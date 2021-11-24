package xxx.xxx.glass.command.message;

import discord4j.rest.util.MultipartRequest;

/**
 * Discord implementation of a ComplexMessage.
 */

public class DiscordComplexMessage implements ComplexMessage {

    private final MultipartRequest multipartRequest;

    public DiscordComplexMessage(final MultipartRequest multipartRequest) {

        this.multipartRequest = multipartRequest;

    }

    /**
     * Used to return the underlying multipart request which can be used
     * to send a complex message to any discord text channel.
     *
     * @return The underlying multipart request.
     */

    public MultipartRequest getMultipartRequest() {

        return multipartRequest;

    }

}
