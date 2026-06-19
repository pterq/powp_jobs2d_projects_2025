package edu.kis.powp.jobs2d.command.catalog;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.observer.Publisher;
import java.util.*;
import java.util.stream.Collectors;

public class CommandCatalog implements ICommandCatalogRepository, ICommandSearchEngine {
    private final Map<String, ICommandEntry> entries = new LinkedHashMap<>();
    private final Publisher changePublisher = new Publisher();

    @Override
    public void addCommand(String name, DriverCommand command) {
        CommandCatalogEntry entry = new CommandCatalogEntry(name, command);
        entries.put(entry.getId(), entry);
        changePublisher.notifyObservers();
    }

    @Override
    public void addCommand(ICommandEntry entry) {
        entries.put(entry.getId(), entry);
        changePublisher.notifyObservers();
    }

    @Override
    public void updateCommand(ICommandEntry entry) {
        if (entry == null || !entries.containsKey(entry.getId())) {
            return;
        }
        entries.put(entry.getId(), entry);
        changePublisher.notifyObservers();
    }

    @Override
    public void removeCommand(String id) {
        entries.remove(id);
        changePublisher.notifyObservers();
    }

    @Override
    public Optional<ICommandEntry> getEntry(String id) {
        return Optional.ofNullable(entries.get(id));
    }

    @Override
    public List<ICommandEntry> getAllEntries() {
        return new ArrayList<>(entries.values());
    }

    @Override
    public List<ICommandEntry> findByName(String namePattern) {
        return entries.values().stream()
                .filter(e -> e.getName().toLowerCase().contains(namePattern.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ICommandEntry> findByTag(String tag) {
        return entries.values().stream()
                .filter(e -> e.hasTag(tag))
                .collect(Collectors.toList());
    }

    @Override
    public List<ICommandEntry> findByTags(List<String> tags) {
        return entries.values().stream()
                .filter(e -> tags.stream().allMatch(searchTag ->
                        e.getTags().stream().anyMatch(t -> t.toLowerCase().contains(searchTag.toLowerCase()))
                ))
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public Publisher getChangePublisher() {
        return changePublisher;
    }
}