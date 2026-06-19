package edu.kis.powp.jobs2d.command.catalog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory and registry for CommandCatalogStorage implementations.
 * Manages multiple storage formats and selects appropriate one based on file extension.
 *
 * Example of extensibility - new formats can be registered without modifying this class.
 */
public class CommandCatalogStorageFactory {
    private static final List<ICommandCatalogStorage> STORAGES = new ArrayList<>();

    static {
        // Register default storage implementations
        register(new FileCommandCatalogStorage());
    }

    /**
     * Register new storage implementation.
     *
     * @param storage storage implementation to register
     */
    public static void register(ICommandCatalogStorage storage) {
        if (storage != null) {
            STORAGES.add(storage);
        }
    }

    /**
     * Find storage implementation suitable for given file.
     *
     * @param file file to determine storage for
     * @return storage implementation that supports the file format
     * @throws IllegalArgumentException if no suitable storage is found
     */
    public static ICommandCatalogStorage findStorage(File file) {
        return STORAGES.stream()
                .filter(storage -> storage.supports(file))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No storage implementation found for file: " + file.getName()));
    }
}




