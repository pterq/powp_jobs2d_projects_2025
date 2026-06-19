package edu.kis.powp.jobs2d.command.catalog;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.observer.Publisher;
import java.util.List;
import java.util.Optional;

public interface ICommandCatalogRepository {
    void addCommand(String name, DriverCommand command);

    void addCommand(ICommandEntry entry);

    void removeCommand(String id);

    Optional<ICommandEntry> getEntry(String id);

    List<ICommandEntry> getAllEntries();

    int size();

    boolean isEmpty();

    Publisher getChangePublisher();
}

