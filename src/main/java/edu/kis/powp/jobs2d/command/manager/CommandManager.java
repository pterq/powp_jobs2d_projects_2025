package edu.kis.powp.jobs2d.command.manager;

import java.util.List;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.importer.CommandImportParser;
import edu.kis.powp.observer.Publisher;
import edu.kis.powp.observer.Subscriber;

/**
 * Command Manager interface.
 */
public interface CommandManager {
    void setCurrentCommand(DriverCommand commandList);

    void setCurrentCommand(List<DriverCommand> commandList, String name);

    void importCurrentCommandFromText(String text, CommandImportParser parser);

    DriverCommand getCurrentCommand();

    void clearCurrentCommand();

    void addSubscriber(Subscriber subscriber);

    List<Subscriber> getSubscribers();

    void deleteObservers();

    void resetObservers();

    String getCurrentCommandString();

    Publisher getChangePublisher();
}
