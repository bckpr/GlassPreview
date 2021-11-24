package xxx.xxx.glass.analyzing.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Contract for Countermeasure implementations.
 */

public interface Countermeasure {

    void execute(final Player target);

    void cancel(final Player target);

    void onEvent(final Event event);

    String getDescription();

}
