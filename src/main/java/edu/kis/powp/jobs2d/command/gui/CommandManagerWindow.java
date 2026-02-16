package edu.kis.powp.jobs2d.command.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.kis.powp.appbase.gui.WindowComponent;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.observer.Subscriber;

public class CommandManagerWindow extends JFrame implements WindowComponent {

    private CommandManager commandManager;
    private CommandPreviewWindow commandPreviewWindow;

    private JTextArea currentCommandField;
    private JTextArea importedCommandField;
    private JButton btnImportCommand;
    private JButton btnApplyTextCommand;
    private JButton btnSaveTextCommand;
    private JButton btnClearObservers;
    private JButton btnRunCommand;
    private JButton btnDuplicateCommand;
    private String observerListString;
    private JTextArea observerListField;
    private String lastImportedExtension;
    private File lastImportedFile;

    private static final long serialVersionUID = 9204679248304669948L;

    public CommandManagerWindow(CommandManager commandManager) {
        this.setTitle("Command Manager");
        this.setSize(700, 700);
        Container content = this.getContentPane();
        content.setLayout(new GridBagLayout());

        this.commandManager = commandManager;

        GridBagConstraints c = new GridBagConstraints();

        observerListField = new JTextArea(4, 40);
        observerListField.setEditable(false);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0.4;
        content.add(observerListField, c);
        updateObserverListField();

        currentCommandField = new JTextArea(6, 40);
        currentCommandField.setEditable(false);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0.7;
        content.add(currentCommandField, c);
        updateCurrentCommandField();

        JLabel importedLabel = new JLabel("Command text (editable):");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0;
        content.add(importedLabel, c);

        importedCommandField = new JTextArea(12, 40);
        importedCommandField.setLineWrap(true);
        importedCommandField.setWrapStyleWord(true);
        JScrollPane importedScroll = new JScrollPane(importedCommandField);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 2.0;
        content.add(importedScroll, c);

        btnRunCommand = new JButton("Run command");
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0;
        content.add(btnRunCommand, c);

        btnDuplicateCommand = new JButton("Duplicate command (deep copy)");
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0;
        content.add(btnDuplicateCommand, c);

        JButton btnClearCommand = new JButton("Clear command");
        btnClearCommand.addActionListener((ActionEvent e) -> this.clearCommand());
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0;
        content.add(btnClearCommand, c);

        btnClearObservers = new JButton("Delete observers");
        btnClearObservers.addActionListener((ActionEvent e) -> this.deleteObservers());
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0;
        content.add(btnClearObservers, c);

        JButton btnEditCommand = new JButton("Edit command");
        btnEditCommand.addActionListener((ActionEvent e) -> new CommandEditorWindow(commandManager, commandPreviewWindow).setVisible(true));
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0;
        content.add(btnEditCommand, c);

        btnImportCommand = new JButton("Import Command");
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0;
        content.add(btnImportCommand, c);

        btnApplyTextCommand = new JButton("Apply text");
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0;
        content.add(btnApplyTextCommand, c);

        btnSaveTextCommand = new JButton("Save command");
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 0;
        content.add(btnSaveTextCommand, c);
    }

    private void clearCommand() {
        commandManager.clearCurrentCommand();
        updateCurrentCommandField();
        importedCommandField.setText("");
        lastImportedExtension = null;
        lastImportedFile = null;
    }

    public void updateCurrentCommandField() {
        currentCommandField.setText(commandManager.getCurrentCommandString());
    }

    public void deleteObservers() {
        if (btnClearObservers.getText().equals("Delete observers")) {
            commandManager.deleteObservers();
            btnClearObservers.setText("Reset observers");
        } else {
            commandManager.resetObservers();
            btnClearObservers.setText("Delete observers");
        }
        this.updateObserverListField();
    }

    public void updateObserverListField() {
        observerListString = "";
        List<Subscriber> commandChangeSubscribers = commandManager.getSubscribers();
        for (Subscriber observer : commandChangeSubscribers) {
            observerListString += observer.toString() + System.lineSeparator();
        }
        if (commandChangeSubscribers.isEmpty())
            observerListString = "No observers loaded";

        observerListField.setText(observerListString);
    }

    @Override
    public void HideIfVisibleAndShowIfHidden() {
        updateObserverListField();
        if (this.isVisible()) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
        }
    }

    public void setImportActionListener(ActionListener actionListener) {
        btnImportCommand.addActionListener(actionListener);
    }

    public void setApplyTextActionListener(ActionListener actionListener) {
        btnApplyTextCommand.addActionListener(actionListener);
    }

    public void setSaveTextActionListener(ActionListener actionListener) {
        btnSaveTextCommand.addActionListener(actionListener);
    }

    public void setRunCommandActionListener(ActionListener actionListener) {
        btnRunCommand.addActionListener(actionListener);
    }

    public void setDuplicateCommandActionListener(ActionListener actionListener) {
        btnDuplicateCommand.addActionListener(actionListener);
    }

    public void setPreviewWindow(CommandPreviewWindow commandPreviewWindow) {
        this.commandPreviewWindow = commandPreviewWindow;
    }

    public void setImportedCommandText(String text, String extension, File file) {
        importedCommandField.setText(text);
        importedCommandField.setCaretPosition(0);
        lastImportedExtension = extension;
        lastImportedFile = file;
    }

    public String getImportedCommandText() {
        return importedCommandField.getText();
    }

    public String getLastImportedExtension() {
        return lastImportedExtension;
    }

    public File getLastImportedFile() {
        return lastImportedFile;
    }
}
