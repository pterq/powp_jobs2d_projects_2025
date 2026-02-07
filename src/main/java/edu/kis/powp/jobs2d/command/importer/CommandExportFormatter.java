package edu.kis.powp.jobs2d.command.importer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

public class CommandExportFormatter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CommandImportTextFormatter csvFormatter = new CommandImportTextFormatter();

    public String format(CommandImportResult result, String extension) {
        if (extension == null) {
            throw new CommandImportException("Missing file extension");
        }
        String normalized = extension.toLowerCase();
        if ("json".equals(normalized)) {
            return formatJson(result);
        }
        if ("xml".equals(normalized)) {
            return formatXml(result);
        }
        if ("csv".equals(normalized) || "txt".equals(normalized)) {
            return csvFormatter.format(result) + System.lineSeparator();
        }
        throw new CommandImportException("Unsupported export extension: " + extension);
    }

    private String formatJson(CommandImportResult result) {
        try {
            Map<String, Object> root = new LinkedHashMap<>();
            if (result.getName() != null && !result.getName().trim().isEmpty()) {
                root.put("name", result.getName().trim());
            }
            root.put("commands", toCommandList(result.getCommands()));
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (Exception ex) {
            throw new CommandImportException("JSON export failed", ex);
        }
    }

    private String formatXml(CommandImportResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("<commands");
        if (result.getName() != null && !result.getName().trim().isEmpty()) {
            sb.append(" name=\"").append(escapeXml(result.getName().trim())).append("\"");
        }
        sb.append(">").append(System.lineSeparator());
        for (DriverCommand command : flattenCommands(result.getCommands())) {
            if (command instanceof SetPositionCommand) {
                SetPositionCommand setPosition = (SetPositionCommand) command;
                sb.append("    <setPosition x=\"")
                    .append(setPosition.getPosX())
                    .append("\" y=\"")
                    .append(setPosition.getPosY())
                    .append("\" />")
                    .append(System.lineSeparator());
            } else if (command instanceof OperateToCommand) {
                OperateToCommand operateTo = (OperateToCommand) command;
                sb.append("    <operateTo x=\"")
                    .append(operateTo.getPosX())
                    .append("\" y=\"")
                    .append(operateTo.getPosY())
                    .append("\" />")
                    .append(System.lineSeparator());
            } else {
                throw new CommandImportException("Unsupported command in XML export");
            }
        }
        sb.append("</commands>").append(System.lineSeparator());
        return sb.toString();
    }

    private List<Map<String, Object>> toCommandList(List<DriverCommand> commands) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DriverCommand command : flattenCommands(commands)) {
            Map<String, Object> entry = new LinkedHashMap<>();
            if (command instanceof SetPositionCommand) {
                SetPositionCommand setPosition = (SetPositionCommand) command;
                entry.put("type", "setPosition");
                entry.put("x", setPosition.getPosX());
                entry.put("y", setPosition.getPosY());
            } else if (command instanceof OperateToCommand) {
                OperateToCommand operateTo = (OperateToCommand) command;
                entry.put("type", "operateTo");
                entry.put("x", operateTo.getPosX());
                entry.put("y", operateTo.getPosY());
            } else {
                throw new CommandImportException("Unsupported command in JSON export");
            }
            list.add(entry);
        }
        return list;
    }

    private List<DriverCommand> flattenCommands(List<DriverCommand> commands) {
        List<DriverCommand> flattened = new ArrayList<>();
        for (DriverCommand command : commands) {
            flattenCommand(command, flattened);
        }
        return flattened;
    }

    private void flattenCommand(DriverCommand command, List<DriverCommand> flattened) {
        if (command instanceof ICompoundCommand) {
            Iterator<DriverCommand> iterator = ((ICompoundCommand) command).iterator();
            while (iterator.hasNext()) {
                flattenCommand(iterator.next(), flattened);
            }
            return;
        }
        flattened.add(command);
    }

    private String escapeXml(String value) {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }
}
