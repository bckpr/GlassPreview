package xxx.xxx.glass.command;

import xxx.xxx.glass.command.commands.Command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simple registry for Command implementations.
 */

public class CommandRegistry {

    private final Set<Command> registeredCommands = new HashSet<>();

    /**
     * Used to register a new command.
     *
     * @param command The command.
     */

    public void registerCommand(final Command command) {

        registeredCommands.add(command);

    }

    /**
     * Used to unregister a command.
     *
     * @param command The command.
     */

    public void unregisterCommand(final Command command) {

        registeredCommands.remove(command);

    }

    /**
     * Used to create and get a copy of all registered
     * commands in a list.
     *
     * @return The copied command list.
     */

    public List<Command> getRegisteredCommands() {

        return new ArrayList<>(registeredCommands);

    }

}
