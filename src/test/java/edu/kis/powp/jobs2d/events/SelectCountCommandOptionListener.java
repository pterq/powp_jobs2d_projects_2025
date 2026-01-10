package edu.kis.powp.jobs2d.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.visitor.CommandCounterVisitor;

/**
 * Listener implementation that counts the number of atomic commands in the current command using CommandCounterVisitor.
 */
public class SelectCountCommandOptionListener implements ActionListener {

    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final CommandManager commandManager;

    public SelectCountCommandOptionListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Triggered when the action is performed.
     * Retrieves the current command, counts its sub-commands, and logs the result.
     * @param e the action event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        DriverCommand command = this.commandManager.getCurrentCommand();

        if (command == null) {
            logger.warning("No command loaded.");
            return;
        }

        CommandCounterVisitor.CommandStats stats = CommandCounterVisitor.countCommands(command);

        logger.info("The command consists of " + stats.getCount() + " single commands.\n" +
                    "setPosition commands: " + stats.getSetPositionCount() + "\n" +
                    "operateTo commands: " + stats.getOperateToCount());
    }
}