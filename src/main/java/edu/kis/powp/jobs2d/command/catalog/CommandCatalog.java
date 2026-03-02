package edu.kis.powp.jobs2d.command.catalog;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.observer.Publisher;
import java.util.*;
import java.util.stream.Collectors;

public class CommandCatalog {
    private final Map<String, CommandCatalogEntry> entries = new LinkedHashMap<>();
    private final Publisher changePublisher = new Publisher();

    public void addCommand(String name, DriverCommand command) {
        CommandCatalogEntry entry = new CommandCatalogEntry(name, command);
        entries.put(entry.getId(), entry);
        changePublisher.notifyObservers();
    }

    public void addCommand(CommandCatalogEntry entry) {
        entries.put(entry.getId(), entry);
        changePublisher.notifyObservers();
    }

    public void removeCommand(String id) {
        entries.remove(id);
        changePublisher.notifyObservers();
    }

    public CommandCatalogEntry getEntry(String id) {
        return entries.get(id);
    }

    public List<CommandCatalogEntry> getAllEntries() {
        return new ArrayList<>(entries.values());
    }

    public List<CommandCatalogEntry> findByName(String namePattern) {
        return entries.values().stream()
                .filter(e -> e.getName().toLowerCase().contains(namePattern.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<CommandCatalogEntry> findByTag(String tag) {
        List<CommandCatalogEntry> result = entries.values().stream()
                .filter(e -> {
                    boolean hasTag = e.hasTag(tag);
                    return hasTag;
                })
                .collect(Collectors.toList());
        return result;
    }

    public List<CommandCatalogEntry> findByTags(List<String> tags) {
        List<CommandCatalogEntry> result = entries.values().stream()
                .filter(e -> {
                    boolean hasAllTags = tags.stream().allMatch(e::hasTag);
                    return hasAllTags;
                })
                .collect(Collectors.toList());
        return result;
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public Publisher getChangePublisher() {
        return changePublisher;
    }
}