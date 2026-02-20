package edu.kis.powp.jobs2d.command;

import edu.kis.powp.jobs2d.command.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * Factory for creating and cataloguing usable commands.
 */
public class CommandFactory {

    private final Map<String, Supplier<DriverCommand>> commandCatalog = new HashMap<>();

    /**
     * Registers a command with a fixed instance (for parameterless commands).
     */
    public void registerCommand(String name, Supplier<DriverCommand> creator) {
        commandCatalog.put(name, creator);
    }

    /**
     * Creates a new instance of a command by its name.
     */
    public DriverCommand createCommand(String name) {
        Supplier<DriverCommand> creator = commandCatalog.get(name);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown command: " + name);
        }
        return creator.get();
    }

    /**
     * Returns a list of available command names.
     */
    public List<String> getAvailableCommands() {
        return new ArrayList<>(commandCatalog.keySet());
    }

    /**
     * Convenience methods for creating parameterized commands
     */
    public DriverCommand createSetPosition(int x, int y) {
        return new SetPositionCommand(x, y);
    }

    public DriverCommand createOperateTo(int x, int y) {
        return new OperateToCommand(x, y);
    }

    /**
     * Builds a compound command from a list of DriverCommand instances.
     */
    public CompoundCommand createCompoundCommand(List<DriverCommand> commands, String name) {
        return CompoundCommand.fromListOfCommands(commands, name);
    }
}
