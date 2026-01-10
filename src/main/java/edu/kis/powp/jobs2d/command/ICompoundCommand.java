package edu.kis.powp.jobs2d.command;

import edu.kis.powp.jobs2d.visitor.CommandVisitor;

import java.util.Iterator;

/**
 * Interface extending Job2dDriverCommand to execute more than one command.
 */
public interface ICompoundCommand extends DriverCommand {

    public Iterator<DriverCommand> iterator();

    @Override
    ICompoundCommand copy();

    /**
     * Accepts a visitor and delegates processing to it.
     * @param visitor the visitor to accept.
     */
    @Override
    default void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
