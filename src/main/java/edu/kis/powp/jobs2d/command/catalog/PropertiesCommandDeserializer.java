package edu.kis.powp.jobs2d.command.catalog;

import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Responsible for deserializing CommandCatalog from Properties format.
 * Follows SRP - single responsibility for deserialization logic only.
 */
public class PropertiesCommandDeserializer {
    private final Properties properties;

    public PropertiesCommandDeserializer(Properties properties) {
        this.properties = properties;
    }

    /**
     * Deserialize catalog from properties.
     *
     * @return deserialized CommandCatalog
     */
    public CommandCatalog deserialize() {
        CommandCatalog catalog = new CommandCatalog();

        int count = Integer.parseInt(properties.getProperty("commands.count", "0"));
        String version = properties.getProperty("version", "1.0");

        for (int i = 0; i < count; i++) {
            CommandCatalogEntry entry = deserializeEntry(i);
            if (entry != null) {
                catalog.addCommand(entry);
            }
        }

        return catalog;
    }

    /**
     * Deserialize single catalog entry.
     *
     * @param index entry index
     * @return deserialized entry or null if parsing fails
     */
    private CommandCatalogEntry deserializeEntry(int index) {
        String prefix = "command." + index + ".";

        String id = properties.getProperty(prefix + "id");
        String name = properties.getProperty(prefix + "command.name");
        if (name == null) {
            // Backward compatibility for older exports.
            name = properties.getProperty(prefix + "name");
        }
        String description = properties.getProperty(prefix + "description");
        String dateStr = properties.getProperty(prefix + "date");
        String tagsStr = properties.getProperty(prefix + "tags", "");

        DriverCommand command = deserializeCommand(prefix + "command.");

        LocalDateTime creationDate = parseCreationDate(dateStr);
        CommandCatalogEntry entry = new CommandCatalogEntry(id, name, command, creationDate);
        entry.setDescription(description);

        if (!tagsStr.isEmpty()) {
            for (String tag : tagsStr.split(",")) {
                if (!tag.trim().isEmpty()) {
                    entry.addTag(tag.trim());
                }
            }
        }

        return entry;
    }

    /**
     * Deserialize driver command recursively.
     *
     * @param prefix property key prefix
     * @return deserialized DriverCommand or null if not found
     */
    private DriverCommand deserializeCommand(String prefix) {
        String type = properties.getProperty(prefix + "type");

        if (type == null) return null;

        switch (type) {
            case "compound":
                return deserializeCompound(prefix);

            case "setposition":
                int setX = Integer.parseInt(properties.getProperty(prefix + "x", "0"));
                int setY = Integer.parseInt(properties.getProperty(prefix + "y", "0"));
                return new SetPositionCommand(setX, setY);

            case "operateto":
                int opX = Integer.parseInt(properties.getProperty(prefix + "x", "0"));
                int opY = Integer.parseInt(properties.getProperty(prefix + "y", "0"));
                return new OperateToCommand(opX, opY);

            default:
                System.err.println("Unknown command type: " + type);
                return null;
        }
    }

    /**
     * Deserialize compound command.
     *
     * @param prefix property key prefix
     * @return deserialized CompoundCommand
     */
    private DriverCommand deserializeCompound(String prefix) {
        int count = Integer.parseInt(properties.getProperty(prefix + "count", "0"));
        List<DriverCommand> commands = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            DriverCommand cmd = deserializeCommand(prefix + "cmd." + i + ".");
            if (cmd != null) {
                commands.add(cmd);
            }
        }

        String compoundName = properties.getProperty(prefix + "name", "Imported Compound");
        return CompoundCommand.fromListOfCommands(commands, compoundName);
    }

    /**
     * Parse creation date from string.
     *
     * @param dateStr date string to parse
     * @return parsed LocalDateTime or null if parsing fails
     */
    private LocalDateTime parseCreationDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateStr.trim());
        } catch (DateTimeParseException ex) {
            // Keeps import resilient for older/custom date formats.
            return null;
        }
    }
}



