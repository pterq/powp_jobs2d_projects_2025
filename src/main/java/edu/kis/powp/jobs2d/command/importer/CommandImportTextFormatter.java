package edu.kis.powp.jobs2d.command.importer;

import java.util.Iterator;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

public class CommandImportTextFormatter {
    public String format(CommandImportResult result) {
        StringBuilder sb = new StringBuilder();
        if (result.getName() != null && !result.getName().trim().isEmpty()) {
            sb.append("name,").append(result.getName().trim()).append(System.lineSeparator());
        }
        for (DriverCommand command : result.getCommands()) {
            appendCommand(command, sb);
        }
        return sb.toString().trim();
    }

    private void appendCommand(DriverCommand command, StringBuilder sb) {
        if (command instanceof ICompoundCommand) {
            Iterator<DriverCommand> iterator = ((ICompoundCommand) command).iterator();
            while (iterator.hasNext()) {
                appendCommand(iterator.next(), sb);
            }
            return;
        }

        if (command instanceof SetPositionCommand) {
            SetPositionCommand setPosition = (SetPositionCommand) command;
            sb.append("setPosition,")
                .append(setPosition.getPosX())
                .append(",")
                .append(setPosition.getPosY())
                .append(System.lineSeparator());
            return;
        }

        if (command instanceof OperateToCommand) {
            OperateToCommand operateTo = (OperateToCommand) command;
            sb.append("operateTo,")
                .append(operateTo.getPosX())
                .append(",")
                .append(operateTo.getPosY())
                .append(System.lineSeparator());
        }
    }
}
