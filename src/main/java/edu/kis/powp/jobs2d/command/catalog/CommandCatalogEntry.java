package edu.kis.powp.jobs2d.command.catalog;

import edu.kis.powp.jobs2d.command.DriverCommand;
import java.time.LocalDateTime;
import java.util.*;

public class CommandCatalogEntry implements ICommandEntry {
    private final String id;
    private String name;
    private final DriverCommand command;
    private final LocalDateTime creationDate;
    private final Set<String> tags;
    private String description;

    // For new created entry in Command Catalog
    public CommandCatalogEntry(String name, DriverCommand command) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.command = command;
        this.creationDate = LocalDateTime.now();
        this.tags = new HashSet<>();
        this.description = "";
    }

    // For imported entries in Command Catalog
    public CommandCatalogEntry(String id, String name, DriverCommand command, LocalDateTime creationDate) {
        this.id = (id != null && !id.trim().isEmpty()) ? id : java.util.UUID.randomUUID().toString();
        this.name = name;
        this.command = command;
        this.creationDate = creationDate != null ? creationDate : LocalDateTime.now();
        this.tags = new HashSet<>();
        this.description = "";
    }


    @Override
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

    @Override
    public void setTags(String tagsString) {
        setTags(Arrays.asList(tagsString.split(",")));
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    @Override
    public DriverCommand getCommand() {
        return command;
    }

    @Override
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public String getDescription() {
        return description != null ? description : "";
    }

    @Override
    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }


    @Override
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            tags.add(tag.trim());
        }
    }

    @Override
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    @Override
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