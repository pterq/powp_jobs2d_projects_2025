package edu.kis.powp.jobs2d.command.catalog;

import java.io.File;
import java.io.IOException;

/**
 * Interface for different storage implementations.
 * Allows adding new storage formats without changing existing code (OCP principle).
 * Works with CommandCatalog model.
 */
public interface ICommandCatalogStorage {
    /**
     * Save command catalog to file.
     *
     * @param catalog catalog to save
     * @param file target file
     * @throws IOException if save operation fails
     */
    void save(CommandCatalog catalog, File file) throws IOException;

    /**
     * Load command catalog from file.
     *
     * @param file source file
     * @return loaded catalog
     * @throws IOException if load operation fails
     */
    CommandCatalog load(File file) throws IOException;

    /**
     * Check if file format is supported by this storage.
     *
     * @param file file to check
     * @return true if file format is supported
     */
    boolean supports(File file);

    /**
     * Get human-readable format description.
     *
     * @return format description
     */
    String getFormatDescription();

    /**
     * Get default file extension filter used by file choosers.
     *
     * @return extension filter pattern (e.g., "*.properties")
     */
    String getFileExtension();
}



