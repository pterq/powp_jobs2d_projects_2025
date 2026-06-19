package edu.kis.powp.jobs2d.command.catalog;

import java.util.List;

public interface ICommandSearchEngine {
    List<ICommandEntry> findByName(String namePattern);

    List<ICommandEntry> findByTag(String tag);

    List<ICommandEntry> findByTags(List<String> tags);
}

