package xxx.xxx.glass.analyzing.actions;

import org.bukkit.event.*;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages all registered Countermeasure implementations and forwards events.
 */

public class CountermeasureManager implements Listener {

    private final static Set<Class<? extends Event>> IGNORED_EVENTS = new HashSet<>(
            Arrays.asList(
                    EntityAirChangeEvent.class
            )
    );

    private final JavaPlugin plugin;
    private final Set<Countermeasure> registeredCountermeasures = new HashSet<>();

    public CountermeasureManager(final JavaPlugin plugin) {

        this.plugin = plugin;

    }

    /**
     * Injects a new pseudo listener into all available event handler lists.
     */

    public void initialize() {

        final RegisteredListener registeredListener = new RegisteredListener(this,
                ((listener, event) -> onEvent(event)), EventPriority.NORMAL, plugin, false);
        HandlerList.getHandlerLists().forEach(handlerList -> handlerList.register(registeredListener));

    }

    /**
     * Gets called when an event gets fired, forwards the event to all
     * Countermeasure implementations unless the event gets ignored
     * because it gets called too often or has no analyzing value.
     *
     * @param event The triggered event instance.
     */

    public void onEvent(final Event event) {

        if (IGNORED_EVENTS.contains(event.getClass())) return;
        if (event instanceof Cancellable && ((Cancellable) event).isCancelled()) return;

        for (final Countermeasure countermeasure : registeredCountermeasures)
            countermeasure.onEvent(event);

    }

    /**
     * Used to get a registered Countermeasure based on the provided class.
     * Will return <code>null</code> if no implementation was found.
     *
     * @param clazz The class.
     * @return The found Countermeasure implementation or <code>null</code> if non was found.
     */

    @Nullable
    public Countermeasure getCountermeasure(@NotNull final Class<? extends Countermeasure> clazz) {

        for (final Countermeasure countermeasure : registeredCountermeasures) {
            if (countermeasure.getClass().equals(clazz))
                return countermeasure;
        }

        return null;

    }

    /**
     * Used to register a new Countermeasure implementation.
     *
     * @param countermeasure The implementation.
     */

    public void registerCountermeasure(@NotNull final Countermeasure countermeasure) {

        registeredCountermeasures.add(countermeasure);

    }

    /**
     * Used to get a new set containing all registered Countermeasure
     * implementations.
     *
     * @return The set of implementations.
     */

    public Set<Countermeasure> getRegisteredCountermeasures() {

        return new HashSet<>(registeredCountermeasures);

    }

}
