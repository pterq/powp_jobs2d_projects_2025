package edu.kis.powp.jobs2d.command.catalog;

import edu.kis.powp.jobs2d.command.DriverCommand;
import java.time.LocalDateTime;

public interface ICommandEntry extends ICommandTaggable {
    String getId();

    String getName();

    void setName(String name);

    DriverCommand getCommand();

    LocalDateTime getCreationDate();

    String getDescription();

    void setDescription(String description);
}

