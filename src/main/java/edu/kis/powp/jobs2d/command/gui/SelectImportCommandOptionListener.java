package edu.kis.powp.jobs2d.command.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.kis.powp.jobs2d.command.importer.CommandImportParser;
import edu.kis.powp.jobs2d.command.manager.CommandManager;

public class SelectImportCommandOptionListener implements ActionListener {
    private final CommandManager commandManager;
    private final CommandImportParser parser;

    public SelectImportCommandOptionListener(CommandManager commandManager, CommandImportParser parser) {
        this.commandManager = commandManager;
        this.parser = parser;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
        int response = fileChooser.showOpenDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(file.toPath()));

                commandManager.importCurrentCommandFromText(content, parser);

                JOptionPane.showMessageDialog(null, "Command imported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error during import: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}