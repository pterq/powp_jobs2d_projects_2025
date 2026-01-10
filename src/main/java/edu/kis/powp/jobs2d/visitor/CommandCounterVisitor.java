package edu.kis.powp.jobs2d.visitor;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

import java.util.Iterator;

public class CommandCounterVisitor implements CommandVisitor {

    private int operateToCount = 0;
    private int setPositionCount = 0;


    private CommandCounterVisitor() {}

    public static class CommandStats {
        private final int operateToCount;
        private final int setPositionCount;

        public CommandStats(int operateToCount, int setPositionCount) {
            this.operateToCount = operateToCount;
            this.setPositionCount = setPositionCount;
        }

        public int getOperateToCount() {
            return operateToCount;
        }

        public int getSetPositionCount() {
            return setPositionCount;
        }

        public int getCount() {
            return operateToCount + setPositionCount;
        }
    }

    public static CommandStats countCommands(DriverCommand command) {
        CommandCounterVisitor visitor = new CommandCounterVisitor();
        command.accept(visitor);
        return new CommandStats(visitor.operateToCount, visitor.setPositionCount);
    }

    @Override
    public void visit(SetPositionCommand setPositionCommand) {
        setPositionCount++;
    }

    @Override
    public void visit(OperateToCommand operateToCommand) {
        operateToCount++;
    }

    @Override
    public void visit(ICompoundCommand iCompoundCommand) {
        Iterator<DriverCommand> iterator = iCompoundCommand.iterator();

        while(iterator.hasNext()) {
            DriverCommand command = iterator.next();
            command.accept(this);
        }
    }
}