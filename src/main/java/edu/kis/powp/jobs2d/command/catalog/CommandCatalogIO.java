package edu.kis.powp.jobs2d.command.catalog;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for backward compatibility and convenient access to Properties-based storage.
 * Delegates to FileCommandCatalogStorage for actual implementation.
 *
 * This class now acts as a facade to the storage layer and keeps a stable API for catalog I/O.
 */
public class CommandCatalogIO {
    private static final ICommandCatalogStorage DEFAULT_STORAGE = new FileCommandCatalogStorage();

    /**
     * Save catalog using storage selected by file extension.
     *
     * @param catalog catalog to save
     * @param file target file
     * @throws IOException if save operation fails
     */
    public static void save(CommandCatalog catalog, File file) throws IOException {
        ICommandCatalogStorage storage = CommandCatalogStorageFactory.findStorage(file);
        storage.save(catalog, file);
    }

    /**
     * Load catalog using storage selected by file extension.
     *
     * @param file source file
     * @return loaded catalog
     * @throws IOException if load operation fails
     */
    public static CommandCatalog load(File file) throws IOException {
        ICommandCatalogStorage storage = CommandCatalogStorageFactory.findStorage(file);
        return storage.load(file);
    }

    /**
     * Save catalog to Properties file.
     *
     * @param catalog catalog to save
     * @param file target file
     * @throws IOException if save operation fails
     */
    public static void saveToProperties(CommandCatalog catalog, File file) throws IOException {
        DEFAULT_STORAGE.save(catalog, file);
    }

    /**
     * Load catalog from Properties file.
     *
     * @param file source file
     * @return loaded catalog
     * @throws IOException if load operation fails
     */
    public static CommandCatalog loadFromProperties(File file) throws IOException {
        return DEFAULT_STORAGE.load(file);
    }

    /**
     * Check if file is a Properties catalog file.
     *
     * @param file file to check
     * @return true if file format is supported
     */
    public static boolean isCatalogFile(File file) {
        return DEFAULT_STORAGE.supports(file);
    }

    /**
     * Get format description.
     *
     * @return format description
     */
    public static String getFormatDescription() {
        return DEFAULT_STORAGE.getFormatDescription();
    }

    /**
     * Get the current storage implementation.
     *
     * @return CommandCatalogStorage instance
     */
    public static ICommandCatalogStorage getStorage() {
        return DEFAULT_STORAGE;
    }
}