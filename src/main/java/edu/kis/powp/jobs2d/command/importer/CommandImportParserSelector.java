package edu.kis.powp.jobs2d.command.importer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandImportParserSelector {
    private final Map<String, CommandImportParser> parsers = new HashMap<>();

    public CommandImportParserSelector() {
        parsers.put("json", new JsonCommandImportParser());
        parsers.put("xml", new XmlCommandImportParser());
        parsers.put("csv", new CsvCommandImportParser());
        parsers.put("txt", new TxtCommandImportParser());
    }

    public CommandImportParser getParserForExtension(String extension) {
        if (extension == null) {
            return null;
        }
        return parsers.get(extension.toLowerCase());
    }

    public CommandImportParser detectParserForText(String text) {
        if (text == null) {
            return null;
        }
        String trimmed = text.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            return parsers.get("json");
        }
        if (trimmed.startsWith("<")) {
            return parsers.get("xml");
        }
        return parsers.get("csv");
    }

    public Set<String> getSupportedExtensions() {
        return Collections.unmodifiableSet(parsers.keySet());
    }
}
