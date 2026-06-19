package edu.kis.powp.jobs2d.command.catalog;

import java.util.Properties;
import edu.kis.powp.jobs2d.command.DriverCommand;

/**
 * Responsible for serializing a CommandCatalog to Properties format.
 * Follows SRP - single responsibility for serialization logic only.
 */
public class PropertiesCommandSerializer {
    private final Properties properties;

    public PropertiesCommandSerializer(Properties properties) {
        this.properties = properties;
    }

    /**
     * Serialize catalog to properties.
     * Stores all entries with their metadata and commands.
     *
     * @param catalog catalog to serialize
     */
    public void serialize(ICommandCatalogRepository catalog) {
        int index = 0;
        for (ICommandEntry entry : catalog.getAllEntries()) {
            serializeEntry(entry, index);
            index++;
        }

        properties.setProperty("commands.count", String.valueOf(index));
        properties.setProperty("version", "1.0");
    }

    /**
     * Serialize single catalog entry.
     *
     * @param entry entry to serialize
     * @param index entry index
     */
    private void serializeEntry(ICommandEntry entry, int index) {
        String prefix = "command." + index + ".";

        properties.setProperty(prefix + "id", entry.getId());
        properties.setProperty(prefix + "date", entry.getCreationDate().toString());
        properties.setProperty(prefix + "description", entry.getDescription());
        properties.setProperty(prefix + "tags", String.join(",", entry.getTags()));

        serializeCommand(entry.getCommand(), prefix + "command.", entry.getName());
    }

    /**
     * Serialize driver command using visitor pattern.
     *
     * @param command command to serialize
     * @param prefix property key prefix
     * @param rootEntryName name of the catalog entry
     */
    private void serializeCommand(DriverCommand command, String prefix, String rootEntryName) {
        CommandSerializer serializer = new CommandSerializer(properties, prefix, rootEntryName);
        command.accept(serializer);
    }

    /**
     * Get serialized properties.
     *
     * @return properties object with serialized catalog
     */
    public Properties getProperties() {
        return properties;
    }
}



