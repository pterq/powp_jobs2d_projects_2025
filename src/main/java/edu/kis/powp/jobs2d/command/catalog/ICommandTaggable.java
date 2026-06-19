package edu.kis.powp.jobs2d.command.catalog;

import java.util.Set;

public interface ICommandTaggable {
    Set<String> getTags();

    boolean hasTag(String tag);
}

