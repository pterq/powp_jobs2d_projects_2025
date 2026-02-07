package edu.kis.powp.jobs2d.command.importer;

import java.util.ArrayList;
import java.util.List;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

public class CsvCommandImportParser implements CommandImportParser {
    @Override
    public CommandImportResult parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new CommandImportException("Input is empty");
        }

        List<DriverCommand> commands = new ArrayList<>();
        String name = null;

        String[] lines = text.split("\\R");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            if (trimmed.matches("\\d+")) {
                continue;
            }

            String[] parts = trimmed.split("\\s*,\\s*");
            if (parts.length >= 2 && parts[0].equalsIgnoreCase("name")) {
                int commaIndex = trimmed.indexOf(',');
                if (commaIndex >= 0 && commaIndex + 1 < trimmed.length()) {
                    name = trimmed.substring(commaIndex + 1).trim();
                }
                continue;
            }

            String type = null;
            String xValue = null;
            String yValue = null;
            if (parts.length == 3) {
                type = parts[0].trim();
                xValue = parts[1].trim();
                yValue = parts[2].trim();
            } else if (parts.length == 4 && parts[0].trim().matches("\\d+")) {
                type = parts[1].trim();
                xValue = parts[2].trim();
                yValue = parts[3].trim();
            } else {
                throw new CommandImportException("Invalid command line: " + trimmed);
            }

            int x = parseInt(xValue, "x");
            int y = parseInt(yValue, "y");
            commands.add(createCommand(type, x, y));
        }

        if (commands.isEmpty()) {
            throw new CommandImportException("No commands found");
        }

        return new CommandImportResult(commands, name);
    }

    private DriverCommand createCommand(String type, int x, int y) {
        if ("setPosition".equalsIgnoreCase(type)) {
            return new SetPositionCommand(x, y);
        }
        if ("operateTo".equalsIgnoreCase(type)) {
            return new OperateToCommand(x, y);
        }
        throw new CommandImportException("Unknown command type: " + type);
    }

    private int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new CommandImportException("Field '" + fieldName + "' must be an integer");
        }
    }
}
