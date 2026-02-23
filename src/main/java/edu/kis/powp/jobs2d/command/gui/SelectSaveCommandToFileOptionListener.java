package edu.kis.powp.jobs2d.command.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import edu.kis.powp.jobs2d.command.importer.CommandCopyFormatter;
import edu.kis.powp.jobs2d.command.importer.CommandExportFormatter;
import edu.kis.powp.jobs2d.command.importer.CommandImportParser;
import edu.kis.powp.jobs2d.command.importer.CommandImportParserSelector;
import edu.kis.powp.jobs2d.command.importer.CommandImportResult;
import edu.kis.powp.jobs2d.command.importer.CommandImportTextFormatter;
import edu.kis.powp.jobs2d.command.manager.CommandManager;

public class SelectSaveCommandToFileOptionListener implements ActionListener {
    private final CommandManager commandManager;
    private final CommandManagerWindow commandManagerWindow;
    private final CommandImportParserSelector parserSelector;
    private final CommandExportFormatter exportFormatter;
    private final CommandCopyFormatter copyFormatter;

    public SelectSaveCommandToFileOptionListener(
            CommandManager commandManager,
            CommandManagerWindow commandManagerWindow,
            CommandImportParserSelector parserSelector,
            CommandExportFormatter exportFormatter) {
        this.commandManager = commandManager;
        this.commandManagerWindow = commandManagerWindow;
        this.parserSelector = parserSelector;
        this.exportFormatter = exportFormatter;
        this.copyFormatter = new CommandCopyFormatter(exportFormatter);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File original = commandManagerWindow.getLastImportedFile();
        if (original == null) {
            JOptionPane.showMessageDialog(null, "No imported file to overwrite.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String text = commandManagerWindow.getImportedCommandText();
        if (text == null || text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Command text is empty.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CommandImportParser parser = parserSelector.detectParserForText(text);
        if (parser == null) {
            JOptionPane.showMessageDialog(null, "Cannot detect command format from text.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            CommandImportResult result = parser.parse(text);
            String extension = getExtension(original.getName());
            if (extension == null) {
                extension = commandManagerWindow.getLastImportedExtension();
            }

            File target = createCopyTarget(original);

            String output = text;
            if ("json".equalsIgnoreCase(extension)) {
                String raw = commandManagerWindow.getLastImportedRawText();
                if (raw != null && !raw.trim().isEmpty()) {
                    output = raw;
                }
            }
            output = copyFormatter.formatCopy(result, extension, output);
            Files.write(target.toPath(), output.getBytes(StandardCharsets.UTF_8));
            commandManagerWindow.setImportedCommandText(output, extension, target);

            commandManager.setCurrentCommand(result.getCommands(), result.getName());
            JOptionPane.showMessageDialog(null, "Command saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error during save: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(dotIndex + 1);
    }

    private File createCopyTarget(File original) {
        String name = original.getName();
        int dotIndex = name.lastIndexOf('.');
        String base = dotIndex > 0 ? name.substring(0, dotIndex) : name;
        String ext = dotIndex > 0 ? name.substring(dotIndex) : "";

        File parent = original.getParentFile();
        File candidate = new File(parent, base + "_copy" + ext);
        int counter = 2;
        while (candidate.exists()) {
            candidate = new File(parent, base + "_copy" + counter + ext);
            counter++;
        }
        return candidate;
    }

}
