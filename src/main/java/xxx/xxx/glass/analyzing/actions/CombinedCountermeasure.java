package xxx.xxx.glass.analyzing.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Countermeasure that combines multiple other implementations.
 */

public class CombinedCountermeasure implements Countermeasure {

    private final Countermeasure[] countermeasures;

    public CombinedCountermeasure(final Countermeasure... countermeasures) {

        this.countermeasures = countermeasures;

    }

    /**
     * Used to execute the countermeasures.
     *
     * @param target The target craft player.
     */

    @Override
    public void execute(final Player target) {

        for (final Countermeasure countermeasure : countermeasures)
            countermeasure.execute(target);

    }

    /**
     * Used to cancel the countermeasures before they expired.
     *
     * @param target The target craft player.
     */

    @Override
    public void cancel(final Player target) {

        for (final Countermeasure countermeasure : countermeasures)
            countermeasure.cancel(target);

    }

    /**
     * Gets called when a new event gets fired.
     *
     * @param event The triggered event instance.
     */

    @Override
    public void onEvent(final Event event) {

    }

    /**
     * Used to return the description of the countermeasure.
     *
     * @return The description.
     */

    @Override
    public String getDescription() {

        final StringBuilder builder = new StringBuilder();
        for (final Countermeasure countermeasure : countermeasures)
            builder.append(countermeasure.getDescription());

        return builder.toString();

    }

    /**
     * Used to get all combined Countermeasure instances.
     *
     * @return The Countermeasure array.
     */

    public Countermeasure[] getCountermeasures() {

        return countermeasures;

    }

}
