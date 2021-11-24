package xxx.xxx.glass.command.arguments;

import java.util.UUID;

/**
 * Contract for Argument implementations.
 */

public interface Argument {

    boolean getAsBoolean();

    int getAsInteger();

    String getAsString();

    UUID getAsUUID();

}
