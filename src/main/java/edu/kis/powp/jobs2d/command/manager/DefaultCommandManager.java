package edu.kis.powp.jobs2d.command.manager;

import java.util.ArrayList;
import java.util.List;

import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.importer.CommandImportParser;
import edu.kis.powp.jobs2d.command.importer.CommandImportResult;
import edu.kis.powp.jobs2d.visitor.CommandCounterVisitor;
import edu.kis.powp.observer.Publisher;
import edu.kis.powp.observer.Subscriber;

/**
 * Default implementation of CommandManager.
 */
public class DefaultCommandManager implements CommandManager {
    private DriverCommand currentCommand = null;
    private List<Subscriber> observersStore = null;

    private final Publisher changePublisher = new Publisher();

    @Override
    public synchronized void setCurrentCommand(DriverCommand commandList) {
        this.currentCommand = commandList;
        changePublisher.notifyObservers();
    }

    @Override
    public synchronized void setCurrentCommand(List<DriverCommand> commandList, String name) {
        CompoundCommand compoundCommand = CompoundCommand.fromListOfCommands(commandList, name);
        setCurrentCommand(compoundCommand);
    }

    @Override
    public synchronized void importCurrentCommandFromText(String text, CommandImportParser parser) {
        CommandImportResult result = parser.parse(text);
        setCurrentCommand(result.getCommands(), result.getName());
    }

    @Override
    public synchronized DriverCommand getCurrentCommand() {
        return currentCommand;
    }

    @Override
    public synchronized void clearCurrentCommand() {
        currentCommand = null;
        changePublisher.notifyObservers();
    }

    @Override
    public synchronized void addSubscriber(Subscriber subscriber) {
        changePublisher.addSubscriber(subscriber);
    }

    @Override
    public synchronized List<Subscriber> getSubscribers() {
        return changePublisher.getSubscribers();
    }

    @Override
    public synchronized void deleteObservers() {
        observersStore = new ArrayList<>(changePublisher.getSubscribers());
        changePublisher.clearObservers();
    }

    @Override
    public synchronized void resetObservers() {
        if (observersStore != null) {
            for (Subscriber subscriber : observersStore) {
                changePublisher.addSubscriber(subscriber);
            }
            observersStore = null;
        }
    }

    @Override
    public synchronized String getCurrentCommandString() {
        if (getCurrentCommand() == null) {
            return "No command loaded";
        } else {
            CommandCounterVisitor.CommandStats stats = CommandCounterVisitor.countCommands(getCurrentCommand());
            StringBuilder sb = new StringBuilder(getCurrentCommand().toString());
            sb.append("\n\nStats:\n");
            sb.append("Total commands: ").append(stats.getCount()).append("\n");
            sb.append("OperateTo count: ").append(stats.getOperateToCount()).append("\n");
            sb.append("SetPosition count: ").append(stats.getSetPositionCount()).append("\n");
            return sb.toString();
        }
    }

    @Override
    public Publisher getChangePublisher() {
        return changePublisher;
    }
}
