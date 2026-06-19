package edu.kis.powp.jobs2d.command.catalog;

import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.command.DriverCommand;
import java.time.LocalDateTime;
import java.util.*;

public class CommandCatalogEntry implements ICommandEntry {
    private final String id;
    private final String name;
    private final DriverCommand command;
    private final LocalDateTime creationDate;
    private final Set<String> tags;
    private final String description;

    // For new created entry in Command Catalog
    public CommandCatalogEntry(String name, DriverCommand command) {
        this(java.util.UUID.randomUUID().toString(), name, command, LocalDateTime.now(), Collections.emptySet(), "");
    }

    // For imported entries in Command Catalog
    public CommandCatalogEntry(String id, String name, DriverCommand command, LocalDateTime creationDate) {
        this(id, name, command, creationDate, Collections.emptySet(), "");
    }

    public CommandCatalogEntry(String id,
                               String name,
                               DriverCommand command,
                               LocalDateTime creationDate,
                               Collection<String> tags,
                               String description) {
        this.id = (id != null && !id.trim().isEmpty()) ? id : java.util.UUID.randomUUID().toString();
        this.name = normalizeName(name);
        this.command = synchronizeCommandName(command, this.name);
        this.creationDate = creationDate != null ? creationDate : LocalDateTime.now();
        this.tags = Collections.unmodifiableSet(normalizeTags(tags));
        this.description = description != null ? description : "";
    }


    @Override
    public ICommandEntry withTags(Collection<String> newTags) {
        return new CommandCatalogEntry(id, name, command, creationDate, newTags, description);
    }

    @Override
    public ICommandEntry withTags(String tagsString) {
        if (tagsString == null || tagsString.trim().isEmpty()) {
            return new CommandCatalogEntry(id, name, command, creationDate, Collections.emptySet(), description);
        }
        return withTags(Arrays.asList(tagsString.split(",")));
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
    public ICommandEntry withName(String name) {
        return new CommandCatalogEntry(id, name, command, creationDate, tags, description);
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
        return description;
    }

    @Override
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    @Override
    public ICommandEntry withDescription(String description) {
        return new CommandCatalogEntry(id, name, command, creationDate, tags, description);
    }


    @Override
    public String toString() {
        return "CommandCatalogEntry{" +
                "name='" + name + '\'' +
                ", tags=" + tags +
                ", creationDate=" + creationDate +
                '}';
    }

    private static DriverCommand synchronizeCommandName(DriverCommand command, String entryName) {
        if (command instanceof CompoundCommand) {
            return ((CompoundCommand) command).withName(entryName);
        }
        return command;
    }

    private static String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Unnamed Command";
        }
        return name.trim();
    }

    private static Set<String> normalizeTags(Collection<String> sourceTags) {
        Set<String> normalized = new LinkedHashSet<>();
        if (sourceTags != null) {
            for (String tag : sourceTags) {
                if (tag != null && !tag.trim().isEmpty()) {
                    normalized.add(tag.trim());
                }
            }
        }
        return normalized;
    }



}