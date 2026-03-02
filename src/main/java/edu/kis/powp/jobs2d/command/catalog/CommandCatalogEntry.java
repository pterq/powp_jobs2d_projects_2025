package edu.kis.powp.jobs2d.command.catalog;

import edu.kis.powp.jobs2d.command.DriverCommand;
import java.time.LocalDateTime;
import java.util.*;

public class CommandCatalogEntry {
    private final String id;
    private String name;
    private final DriverCommand command;
    private final LocalDateTime creationDate;
    private final Set<String> tags;
    private String description;

    public CommandCatalogEntry(String name, DriverCommand command) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.command = command;
        this.creationDate = LocalDateTime.now();
        this.tags = new HashSet<>();
        this.description = "";
    }


    public void setTags(Collection<String> newTags) {
        tags.clear();
        if (newTags != null) {
            for (String tag : newTags) {
                if (tag != null && !tag.trim().isEmpty()) {
                    tags.add(tag.trim());
                }
            }
        }
    }

    public void setTags(String tagsString) {
        setTags(Arrays.asList(tagsString.split(",")));
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public DriverCommand getCommand() {
        return command;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }


    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            tags.add(tag.trim());
        }
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public void addTags(Set<String> newTags) {
        if (newTags != null) {
            tags.addAll(newTags);
        }
    }


    @Override
    public String toString() {
        return "CommandCatalogEntry{" +
                "name='" + name + '\'' +
                ", tags=" + tags +
                ", creationDate=" + creationDate +
                '}';
    }



}