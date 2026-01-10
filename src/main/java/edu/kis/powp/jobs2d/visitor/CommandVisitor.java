package edu.kis.powp.jobs2d.visitor;

import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

/**
 * Interface for the Visitor pattern to traverse and process driver commands.
 */
public interface CommandVisitor {

    /**
     * Visits a SetPositionCommand.
     * * @param setPositionCommand the command to visit.
     */
    void visit(SetPositionCommand setPositionCommand);

    /**
     * Visits an OperateToCommand.
     * * @param operateToCommand the command to visit.
     */
    void visit(OperateToCommand operateToCommand);

    /**
     * Visits a compound command.
     * * @param iCompoundCommand the compound command to visit.
     */
    void visit(ICompoundCommand iCompoundCommand);
}