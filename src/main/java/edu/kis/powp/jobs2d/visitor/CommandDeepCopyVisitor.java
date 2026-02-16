package edu.kis.powp.jobs2d.visitor;

import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Visitor that produces a deep copy of a command (including nested compound commands).
 */
public class CommandDeepCopyVisitor implements CommandVisitor {

    private DriverCommand copy;

    public CommandDeepCopyVisitor() {
    }

    public static DriverCommand createDeepCopyOf(DriverCommand command) {
        CommandDeepCopyVisitor visitor = new CommandDeepCopyVisitor();
        command.accept(visitor);
        return visitor.getCopy();
    }

    public static DriverCommand deepCopy(DriverCommand command) {
        return createDeepCopyOf(command);
    }

    public DriverCommand getCopy() {
        return copy;
    }

    @Override
    public void visit(SetPositionCommand setPositionCommand) {
        copy = new SetPositionCommand(setPositionCommand.getPosX(), setPositionCommand.getPosY());
    }

    @Override
    public void visit(OperateToCommand operateToCommand) {
        copy = new OperateToCommand(operateToCommand.getPosX(), operateToCommand.getPosY());
    }

    @Override
    public void visit(ICompoundCommand compoundCommand) {
        List<DriverCommand> copiedCommands = new ArrayList<>();
        Iterator<DriverCommand> iterator = compoundCommand.iterator();

        while (iterator.hasNext()) {
            iterator.next().accept(this);
            copiedCommands.add(copy);
        }

        copy = CompoundCommand.fromListOfCommands(
                copiedCommands,
                compoundCommand.toString() + "_copy"
        );
    }
}
