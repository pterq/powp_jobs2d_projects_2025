package edu.kis.powp.jobs2d.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.visitor.CommandDeepCopyVisitor;

public class SelectDuplicateCommandOptionListener implements ActionListener {

    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final CommandManager commandManager;

    public SelectDuplicateCommandOptionListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DriverCommand command = commandManager.getCurrentCommand();
        if (command == null) {
            logger.warning("No command loaded.");
            return;
        }

        DriverCommand copy = CommandDeepCopyVisitor.createDeepCopyOf(command);
        commandManager.setCurrentCommand(copy);
        logger.info("Command duplicated (deep copy).");
    }
}
