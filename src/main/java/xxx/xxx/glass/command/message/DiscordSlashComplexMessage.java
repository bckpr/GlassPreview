package xxx.xxx.glass.command.message;

import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;

import java.util.function.Consumer;

/**
 * Discord slash command implementation of a ComplexMessage.
 */

public class DiscordSlashComplexMessage implements ComplexMessage {

    private final Consumer<InteractionApplicationCommandCallbackSpec> specConsumer;

    public DiscordSlashComplexMessage(final Consumer<InteractionApplicationCommandCallbackSpec> specConsumer) {

        this.specConsumer = specConsumer;

    }

    /**
     * Used to get the underlying spec which can be used to respond to
     * a discord command input.
     *
     * @return The underlying spec.
     */

    public Consumer<InteractionApplicationCommandCallbackSpec> getSpecConsumer() {

        return specConsumer;

    }

}
