package edu.kis.powp.jobs2d.command.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import edu.kis.powp.jobs2d.command.importer.CommandImportParser;
import edu.kis.powp.jobs2d.command.importer.CommandImportParserSelector;
import edu.kis.powp.jobs2d.command.importer.CommandImportResult;
import edu.kis.powp.jobs2d.command.manager.CommandManager;

public class SelectImportCommandFromTextOptionListener implements ActionListener {
    private final CommandManager commandManager;
    private final CommandImportParserSelector parserSelector;
    private final CommandManagerWindow commandManagerWindow;

    public SelectImportCommandFromTextOptionListener(
            CommandManager commandManager,
            CommandImportParserSelector parserSelector,
            CommandManagerWindow commandManagerWindow) {
        this.commandManager = commandManager;
        this.parserSelector = parserSelector;
        this.commandManagerWindow = commandManagerWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = commandManagerWindow.getImportedCommandText();
        if (text == null || text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No command text to import.", "Import Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CommandImportParser parser = parserSelector.detectParserForText(text);
        if (parser == null) {
            String extension = commandManagerWindow.getLastImportedExtension();
            parser = parserSelector.getParserForExtension(extension);
        }

        try {
            CommandImportResult result = parser.parse(text);
            commandManager.setCurrentCommand(result.getCommands(), result.getName());
            String extension = commandManagerWindow.getLastImportedExtension();
            if (extension == null) {
                extension = "csv";
            }
            commandManagerWindow.setImportedCommandTextFromFile(
                text,
                extension,
                commandManagerWindow.getLastImportedFile());
            JOptionPane.showMessageDialog(null, "Command imported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error during import: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
