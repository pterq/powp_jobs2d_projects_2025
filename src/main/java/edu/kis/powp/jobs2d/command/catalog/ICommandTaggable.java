package edu.kis.powp.jobs2d.command.catalog;

import java.util.Collection;
import java.util.Set;

public interface ICommandTaggable {
    Set<String> getTags();

    void setTags(Collection<String> newTags);

    void setTags(String tagsString);

    void addTag(String tag);

    void removeTag(String tag);

    boolean hasTag(String tag);

    void addTags(Set<String> newTags);
}

