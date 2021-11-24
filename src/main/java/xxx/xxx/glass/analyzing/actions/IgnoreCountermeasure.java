package xxx.xxx.glass.analyzing.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Blank Countermeasure implementation to do nothing.
 */

public class IgnoreCountermeasure implements Countermeasure {

    /**
     * Used to execute the countermeasure.
     *
     * @param target The target craft player.
     */

    @Override
    public void execute(final Player target) {

    }

    /**
     * Used to cancel the countermeasure before it expired.
     *
     * @param target The target craft player.
     */

    @Override
    public void cancel(final Player target) {

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

        return "Do nothing.";

    }

}
