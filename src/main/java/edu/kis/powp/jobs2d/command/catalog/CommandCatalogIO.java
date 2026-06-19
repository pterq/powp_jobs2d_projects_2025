package edu.kis.powp.jobs2d.command.catalog;

import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.command.DriverCommand;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;


public class CommandCatalogIO {

    public static void saveToProperties(CommandCatalog catalog, File file) throws IOException {
        Properties props = new Properties();

        int index = 0;
        for (CommandCatalogEntry entry : catalog.getAllEntries()) {
            String prefix = "command." + index + ".";
            props.setProperty(prefix + "id", entry.getId());
            props.setProperty(prefix + "date", entry.getCreationDate().toString());
            props.setProperty(prefix + "description", entry.getDescription());
            props.setProperty(prefix + "tags", String.join(",", entry.getTags()));

            serializeCommand(entry.getCommand(), props, prefix + "command.", entry.getName());

            index++;
        }

        props.setProperty("commands.count", String.valueOf(index));
        props.setProperty("version", "1.0");

        try (FileOutputStream out = new FileOutputStream(file)) {
            props.store(out, "Command Catalog - Full Command Data Included");
        }
    }

    private static void serializeCommand(DriverCommand command, Properties props, String prefix, String rootEntryName) {
        CommandSerializer serializer = new CommandSerializer(props, prefix, rootEntryName);
        command.accept(serializer);
    }


    public static CommandCatalog loadFromProperties(File file) throws IOException {
        CommandCatalog catalog = new CommandCatalog();
        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
        }

        int count = Integer.parseInt(props.getProperty("commands.count", "0"));
        String version = props.getProperty("version", "1.0");

        for (int i = 0; i < count; i++) {
            String prefix = "command." + i + ".";

            String id = props.getProperty(prefix + "id");
            String name = props.getProperty(prefix + "command.name");
            if (name == null) {
                // Backward compatibility for older exports.
                name = props.getProperty(prefix + "name");
            }
            String description = props.getProperty(prefix + "description");
            String dateStr = props.getProperty(prefix + "date");
            String tagsStr = props.getProperty(prefix + "tags", "");
            String commandType = props.getProperty(prefix + "command.type");

            DriverCommand command = deserializeCommand(props, prefix + "command.");

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

            catalog.addCommand(entry);
        }

        return catalog;
    }

    private static LocalDateTime parseCreationDate(String dateStr) {
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

    private static DriverCommand deserializeCommand(Properties props, String prefix) {
        String type = props.getProperty(prefix + "type");

        if (type == null) return null;

        switch (type) {
            case "compound":
                int count = Integer.parseInt(props.getProperty(prefix + "count", "0"));
                List<DriverCommand> commands = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    DriverCommand cmd = deserializeCommand(props, prefix + "cmd." + i + ".");
                    if (cmd != null) {
                        commands.add(cmd);
                    }
                }

                String compoundName = props.getProperty(prefix + "name", "Imported Compound");
                return CompoundCommand.fromListOfCommands(commands, compoundName);

            case "setposition":
                int setX = Integer.parseInt(props.getProperty(prefix + "x", "0"));
                int setY = Integer.parseInt(props.getProperty(prefix + "y", "0"));
                return new SetPositionCommand(setX, setY);

            case "operateto":
                int opX = Integer.parseInt(props.getProperty(prefix + "x", "0"));
                int opY = Integer.parseInt(props.getProperty(prefix + "y", "0"));
                return new OperateToCommand(opX, opY);

            default:
                System.err.println("Unknown command type: " + type);
                return null;
        }
    }

    public static boolean isCatalogFile(File file) {
        return file.getName().toLowerCase().endsWith(".properties");
    }

    public static String getFormatDescription() {
        return "Properties file format with full command serialization";
    }
}