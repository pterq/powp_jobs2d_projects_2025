package edu.kis.powp.jobs2d.command.catalog;

import edu.kis.powp.jobs2d.command.DriverCommand;
import java.time.LocalDateTime;
import java.util.Collection;

public interface ICommandEntry extends ICommandTaggable {
    String getId();

    String getName();

    ICommandEntry withName(String name);

    DriverCommand getCommand();

    LocalDateTime getCreationDate();

    String getDescription();

    ICommandEntry withDescription(String description);

    ICommandEntry withTags(Collection<String> tags);

    ICommandEntry withTags(String tagsString);
}

