package edu.kis.powp.jobs2d.command.catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Implements CommandCatalogStorage for Properties file format.
 * Responsible only for file I/O operations.
 * Delegates serialization/deserialization to specialized classes.
 */
public class FileCommandCatalogStorage implements ICommandCatalogStorage {

    @Override
    public void save(CommandCatalog catalog, File file) throws IOException {
        Properties props = new Properties();
        PropertiesCommandSerializer serializer = new PropertiesCommandSerializer(props);
        serializer.serialize(catalog);

        try (FileOutputStream out = new FileOutputStream(file)) {
            props.store(out, "Command Catalog - Full Command Data Included");
        }
    }

    @Override
    public CommandCatalog load(File file) throws IOException {
        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
        }

        PropertiesCommandDeserializer deserializer = new PropertiesCommandDeserializer(props);
        return deserializer.deserialize();
    }

    @Override
    public boolean supports(File file) {
        return file != null && file.getName().toLowerCase().endsWith(".properties");
    }

    @Override
    public String getFormatDescription() {
        return "Properties file format with full command serialization";
    }

    @Override
    public String getFileExtension() {
        return "*.properties";
    }
}



