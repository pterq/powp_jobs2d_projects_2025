package edu.kis.powp.jobs2d.command;

import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.visitor.CommandVisitor;

/**
 * DriverCommand interface.
 */
public interface DriverCommand {

    /**
     * Execute command on driver.
     * 
     * @param driver driver.
     */
    public void execute(Job2dDriver driver);

    DriverCommand copy();

    /**
     * Accepts a visitor for this command.
     * @param visitor the visitor to accept.
     */
    public void accept(CommandVisitor visitor);
}
