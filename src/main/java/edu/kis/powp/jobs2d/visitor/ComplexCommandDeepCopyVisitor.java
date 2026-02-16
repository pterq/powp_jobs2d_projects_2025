package edu.kis.powp.jobs2d.visitor;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

/**
 * Backward-compatible deep copy visitor for complex (compound) commands.
 */
public class ComplexCommandDeepCopyVisitor implements CommandVisitor {

    private final CommandDeepCopyVisitor delegate = new CommandDeepCopyVisitor();

    public static DriverCommand copy(DriverCommand command) {
        return CommandDeepCopyVisitor.createDeepCopyOf(command);
    }

    public DriverCommand getCopy() {
        return delegate.getCopy();
    }

    @Override
    public void visit(SetPositionCommand setPositionCommand) {
        delegate.visit(setPositionCommand);
    }

    @Override
    public void visit(OperateToCommand operateToCommand) {
        delegate.visit(operateToCommand);
    }

    @Override
    public void visit(ICompoundCommand iCompoundCommand) {
        delegate.visit(iCompoundCommand);
    }
}
