package edu.kis.powp.jobs2d.command.importer;

public class CommandCopyFormatter {
    private final CommandExportFormatter exportFormatter;

    public CommandCopyFormatter(CommandExportFormatter exportFormatter) {
        this.exportFormatter = exportFormatter;
    }

    public String formatCopy(CommandImportResult result, String extension, String originalText) {
        if (extension == null) {
            throw new IllegalArgumentException("File extension is required.");
        }
        if (originalText != null) {
            return originalText;
        }
        return exportFormatter.format(result, extension);
    }
}
