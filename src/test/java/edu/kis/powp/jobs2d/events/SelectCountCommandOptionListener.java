package edu.kis.powp.jobs2d.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.features.CommandsFeature;
import edu.kis.powp.jobs2d.visitor.CounterVisitor;

public class SelectCountCommandOptionListener implements ActionListener {

    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    public void actionPerformed(ActionEvent e) {
        DriverCommand command = CommandsFeature.getDriverCommandManager().getCurrentCommand();

        if (command == null) {
            logger.warning("No command loaded.");
            return;
        }

        logger.info("The command consists of " + CounterVisitor.countCommands(command) + " single commands.");
    }
}