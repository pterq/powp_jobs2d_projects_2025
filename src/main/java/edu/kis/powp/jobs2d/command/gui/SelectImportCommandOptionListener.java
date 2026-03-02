package edu.kis.powp.jobs2d.command.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.kis.powp.jobs2d.command.importer.CommandImportParser;
import edu.kis.powp.jobs2d.command.importer.CommandImportResult;
import edu.kis.powp.jobs2d.command.importer.CommandImportParserSelector;
import edu.kis.powp.jobs2d.command.manager.CommandManager;

public class SelectImportCommandOptionListener implements ActionListener {
    private final CommandManager commandManager;
    private final CommandImportParserSelector parserSelector;
    private final CommandManagerWindow commandManagerWindow;

    public SelectImportCommandOptionListener(
            CommandManager commandManager,
            CommandImportParserSelector parserSelector,
            CommandManagerWindow commandManagerWindow) {
        this.commandManager = commandManager;
        this.parserSelector = parserSelector;
        this.commandManagerWindow = commandManagerWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Command files (*.json, *.xml, *.csv, *.txt)",
                "json",
                "xml",
                "csv",
                "txt"));
        int response = fileChooser.showOpenDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                String extension = getExtension(file.getName());
                CommandImportParser parser = parserSelector.getParserForExtension(extension);
                if (parser == null) {
                    Set<String> supported = parserSelector.getSupportedExtensions();
                    JOptionPane.showMessageDialog(
                            null,
                            "Unsupported file extension. Supported: " + supported,
                            "Import Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                CommandImportResult result = parser.parse(content);
                commandManager.setCurrentCommand(result.getCommands(), result.getName());
                if (commandManagerWindow != null) {
                    commandManagerWindow.setImportedCommandTextFromFile(content, extension, file);
                }

                JOptionPane.showMessageDialog(null, "Command imported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error during import: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(dotIndex + 1);
    }
}
