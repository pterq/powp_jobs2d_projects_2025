package edu.kis.powp.jobs2d.command.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import edu.kis.powp.appbase.gui.WindowComponent;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.drivers.DriverManager;
import edu.kis.powp.observer.Subscriber;

public class CommandManagerWindow extends JFrame implements WindowComponent {

    private CommandManager commandManager;
    private DriverManager driverManager;

    private JTextArea currentCommandField;
    private JButton btnImportCommand;
    private JButton btnClearObservers;
    private JButton btnRunCommand;
    private String observerListString;
    private JTextArea observerListField;
    private List<Subscriber> observersStore = null;

    /**
     *
     */
    private static final long serialVersionUID = 9204679248304669948L;

    public CommandManagerWindow(CommandManager commandManager, DriverManager driverManager) {
        this.setTitle("Command Manager");
        this.setSize(400, 400);
        Container content = this.getContentPane();
        content.setLayout(new GridBagLayout());

        this.commandManager = commandManager;
        this.driverManager = driverManager;

        GridBagConstraints c = new GridBagConstraints();

        observerListField = new JTextArea("");
        observerListField.setEditable(false);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 1;
        content.add(observerListField, c);
        updateObserverListField();

        currentCommandField = new JTextArea("");
        currentCommandField.setEditable(false);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 1;
        content.add(currentCommandField, c);
        updateCurrentCommandField();

        btnRunCommand = new JButton("Run command");
        btnRunCommand.addActionListener((ActionEvent e) -> this.runCommand());
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 1;
        content.add(btnRunCommand, c);

        JButton btnClearCommand = new JButton("Clear command");
        btnClearCommand.addActionListener((ActionEvent e) -> this.clearCommand());
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 1;
        content.add(btnClearCommand, c);

        btnClearObservers = new JButton("Delete observers");
        btnClearObservers.addActionListener((ActionEvent e) -> this.deleteObservers());
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 1;
        content.add(btnClearObservers, c);

        btnImportCommand = new JButton("Import JSON");
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.weighty = 1;
        content.add(btnImportCommand, c);
    }

    private void clearCommand() {
        commandManager.clearCurrentCommand();
        updateCurrentCommandField();
    }

    private void runCommand() {
        if (commandManager.getCurrentCommand() != null) {
            commandManager.getCurrentCommand().execute(driverManager.getCurrentDriver());
        }
    }

    public void updateCurrentCommandField() {
        currentCommandField.setText(commandManager.getCurrentCommandString());
    }

    public void deleteObservers() {
        if (btnClearObservers.getText().equals("Delete observers")) {
            observersStore = new ArrayList<>(commandManager.getChangePublisher().getSubscribers());
            commandManager.getChangePublisher().clearObservers();
            btnClearObservers.setText("Reset observers");
        } else {
            if (observersStore != null) {
                for (Subscriber subscriber : observersStore) {
                    commandManager.getChangePublisher().addSubscriber(subscriber);
                }
            }
            btnClearObservers.setText("Delete observers");
        }
        this.updateObserverListField();
    }

    public void updateObserverListField() {
        observerListString = "";
        List<Subscriber> commandChangeSubscribers = commandManager.getChangePublisher().getSubscribers();
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

    public void setImportActionListener(java.awt.event.ActionListener actionListener) {
        btnImportCommand.addActionListener(actionListener);
    }

}
