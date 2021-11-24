package xxx.xxx.glass.discord;

import xxx.xxx.glass.common.ServiceProvider;
import discord4j.core.event.domain.interaction.ButtonInteractEvent;

import java.util.function.Consumer;

/**
 * Handles custom discord button clicks.
 */

public class ButtonClickHandler implements Consumer<ButtonInteractEvent> {

    private final ButtonRegistry buttonRegistry;

    public ButtonClickHandler(final ServiceProvider serviceProvider) {

        this.buttonRegistry = serviceProvider.getService(ButtonRegistry.class);

    }

    /**
     * Gets called when a button on a message gets clicked, checks if the
     * clicked button is registered and if so calls the mapped consumer.
     *
     * @param buttonInteractEvent The triggered event instance.
     */

    @Override
    public void accept(final ButtonInteractEvent buttonInteractEvent) {

        if (buttonRegistry.contains(buttonInteractEvent.getCustomId()))
            buttonRegistry.get(buttonInteractEvent.getCustomId()).accept(buttonInteractEvent);

    }

}
