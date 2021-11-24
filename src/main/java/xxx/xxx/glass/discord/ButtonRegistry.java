package xxx.xxx.glass.discord;

import discord4j.core.event.domain.interaction.ButtonInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Registry for discord buttons to map event consumers.
 */

public class ButtonRegistry {

    private final Map<String, Consumer<ButtonInteractEvent>> registeredButtons = new HashMap<>();

    /**
     * Used to register a button id and map it to an event consumer.
     *
     * @param id       The button id.
     * @param consumer The event consumer.
     */

    public void registerButton(final String id, final Consumer<ButtonInteractEvent> consumer) {

        registeredButtons.put(id, consumer);

    }

    /**
     * Used to unregister a registered button id.
     *
     * @param id The button id.
     */

    public void unregisterButton(final String id) {

        registeredButtons.remove(id);

    }

    /**
     * Used to check if the provided button id is registered.
     *
     * @param id The button id.
     * @return <code>true</code> if the button id is registered, <code>false</code> otherwise.
     */

    public boolean contains(final String id) {

        return registeredButtons.containsKey(id);

    }

    /**
     * Used to get a registered event consumer by the provided button id.
     * Will return <code>null</code> if the button id isn't registered.
     *
     * @param id The button id.
     * @return The found event consumer or <code>null</code> if the button id is not registered.
     */

    public Consumer<ButtonInteractEvent> get(final String id) {

        return registeredButtons.get(id);

    }

}
