package edu.kis.powp.jobs2d.command.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;
import edu.kis.powp.jobs2d.visitor.CommandVisitor;

/**
 * Visitor responsible for serializing commands to catalog properties format.
 */
public class CommandSerializer implements CommandVisitor {

    private final Properties props;
    private final String prefix;
    private final String rootEntryName;

    public CommandSerializer(Properties props, String prefix, String rootEntryName) {
        this.props = props;
        this.prefix = prefix;
        this.rootEntryName = rootEntryName;
    }

    @Override
    public void visit(SetPositionCommand setPositionCommand) {
        props.setProperty(prefix + "type", "setposition");
        props.setProperty(prefix + "x", String.valueOf(setPositionCommand.getPosX()));
        props.setProperty(prefix + "y", String.valueOf(setPositionCommand.getPosY()));
    }

    @Override
    public void visit(OperateToCommand operateToCommand) {
        props.setProperty(prefix + "type", "operateto");
        props.setProperty(prefix + "x", String.valueOf(operateToCommand.getPosX()));
        props.setProperty(prefix + "y", String.valueOf(operateToCommand.getPosY()));
    }

    @Override
    public void visit(ICompoundCommand iCompoundCommand) {
        props.setProperty(prefix + "type", "compound");
        String serializedName = rootEntryName != null ? rootEntryName : iCompoundCommand.toString();
        props.setProperty(prefix + "name", serializedName);

        List<DriverCommand> commands = new ArrayList<>();
        iCompoundCommand.iterator().forEachRemaining(commands::add);
        props.setProperty(prefix + "count", String.valueOf(commands.size()));

        for (int i = 0; i < commands.size(); i++) {
            CommandSerializer nestedSerializer = new CommandSerializer(props, prefix + "cmd." + i + ".", null);
            commands.get(i).accept(nestedSerializer);
        }
    }
}

