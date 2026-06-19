
package edu.kis.powp.jobs2d.command.gui.catalog;

import edu.kis.powp.appbase.gui.WindowComponent;
import edu.kis.powp.jobs2d.command.catalog.CommandCatalog;
import edu.kis.powp.jobs2d.command.catalog.ICommandCatalogRepository;
import edu.kis.powp.jobs2d.command.catalog.ICommandEntry;
import edu.kis.powp.jobs2d.command.catalog.ICommandCatalogStorage;
import edu.kis.powp.jobs2d.command.catalog.ICommandSearchEngine;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.observer.Subscriber;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import edu.kis.powp.jobs2d.command.DriverCommand;


public class CommandCatalogWindow extends JFrame implements WindowComponent, Subscriber {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final ICommandCatalogRepository catalog;
    private final ICommandSearchEngine searchEngine;
    private final CommandManager commandManager;
    private final ICommandCatalogStorage catalogStorage;
    private JTable catalogTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;

    public CommandCatalogWindow(ICommandCatalogRepository catalog,
                                ICommandSearchEngine searchEngine,
                                CommandManager commandManager,
                                ICommandCatalogStorage catalogStorage) {
        this.catalog = catalog;
        this.searchEngine = searchEngine;
        this.commandManager = commandManager;
        this.catalogStorage = catalogStorage;
        this.catalog.getChangePublisher().addSubscriber(this);
        this.commandManager.getChangePublisher().addSubscriber(this);

        setTitle("Command Catalog");
        setSize(800, 500);
        setLayout(new BorderLayout());

        initializeComponents();
        refreshTable();
    }

    private void initializeComponents() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));

        searchField = new JTextField(20);
        searchField.addActionListener(e -> performSearch());
        searchPanel.add(searchField);

        searchTypeCombo = new JComboBox<>(new String[]{"Name", "Tags"});
        searchPanel.add(searchTypeCombo);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> performSearch());
        searchPanel.add(btnSearch);

        JButton btnClear = new JButton("Clear Search");
        btnClear.addActionListener(e -> clearSearch());
        searchPanel.add(btnClear);

        add(searchPanel, BorderLayout.NORTH);


        String[] columnNames = {"Name", "Description", "Tags", "Created", "Actions", "ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 1 || column == 2; // Editable: 0 - Name, 1 - Description, 2 - Tags
            }

            @Override
            public void setValueAt(Object value, int row, int column) {
                String name = (String) getValueAt(row, 0);
                List<ICommandEntry> entries = searchEngine.findByName(name);



                if (!entries.isEmpty()) {
                    ICommandEntry entry = entries.get(0);
                    if (column == 0) { // Name column
                        String previousName = entry.getName();
                        String newName = (String) value;
                        entry.setName(newName);
                        logger.info("Name for: '" + name + "' updated: before='" + previousName + "', after='" + newName + "'");
                    } else if (column == 1) { // Description column
                        String previousDescription = entry.getDescription();
                        String newDescription = (String) value;
                        entry.setDescription(newDescription);
                        logger.info("Description for: '" + name + "' updated: before='" + previousDescription + "', after='" + newDescription + "'");
                    } else if (column == 2) { // Tags column
                        String previousTags = String.join(", ", entry.getTags());
                        String newTags = (String) value;
                        entry.setTags(newTags);
                        logger.info("Tags for: '" + name + "'  updated: before='" + previousTags + "', after='" + newTags + "'");
                    }
                }
                super.setValueAt(value, row, column);
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        catalogTable = new JTable(tableModel);
        catalogTable.setRowSelectionAllowed(true);
        catalogTable.setCellSelectionEnabled(false);
        catalogTable.setColumnSelectionAllowed(false);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Commits edited cell value when focus moves to another component (e.g. Export button).
        catalogTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        catalogTable.removeColumn(catalogTable.getColumnModel().getColumn(5));
        catalogTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());

        JScrollPane scrollPane = new JScrollPane(catalogTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton btnAdd = new JButton("Add Current Command");
        btnAdd.addActionListener(e -> addCurrentCommand());
        buttonPanel.add(btnAdd);

        JButton btnImport = new JButton("Import Catalog");
        btnImport.addActionListener(e -> importCatalog());
        buttonPanel.add(btnImport);

        JButton btnExport = new JButton("Export Catalog");
        btnExport.addActionListener(e -> exportCatalog());
        buttonPanel.add(btnExport);

        JButton btnShowCurrent = new JButton("Show Current Command");
        btnShowCurrent.addActionListener(e -> showCurrentCommand());
        buttonPanel.add(btnShowCurrent);

        add(buttonPanel, BorderLayout.SOUTH);


        catalogTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());

        catalogTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = catalogTable.rowAtPoint(e.getPoint());
                int col = catalogTable.columnAtPoint(e.getPoint());

                if (col == 4) {
                    String name = (String) tableModel.getValueAt(row, 0);
                    List<ICommandEntry> entries = searchEngine.findByName(name);
                    if (!entries.isEmpty()) {

                        commandManager.setCurrentCommand(entries.get(0).getCommand());


                        JOptionPane.showMessageDialog(CommandCatalogWindow.this,
                                "Command loaded: " + name);

                        catalogTable.setRowSelectionInterval(row, row);
                    }
                }
            }
        });
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        String searchType = (String) searchTypeCombo.getSelectedItem();

        List<ICommandEntry> results;

        if (query.isEmpty()) {
            results = catalog.getAllEntries();
        } else {
            switch (Objects.requireNonNull(searchType)) {
                case "Name":
                    results = searchEngine.findByName(query);
                    break;
                case "Tags":
                    List<String> tags = Arrays.stream(query.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                    results = searchEngine.findByTags(tags);
                    break;
                default:
                    results = catalog.getAllEntries();
            }
        }

        updateTable(results);
    }

    private void clearSearch() {
        if (searchField != null) {
            searchField.setText("");
        }
        refreshTable();
    }

    private void refreshTable() {
        List<ICommandEntry> allEntries = catalog.getAllEntries();
        updateTable(allEntries);
    }

    private void updateTable(List<ICommandEntry> entries) {
        tableModel.setRowCount(0);
        for (ICommandEntry entry : entries) {
            tableModel.addRow(new Object[]{
                    entry.getName(),
                    entry.getDescription(),
                    String.join(", ", entry.getTags()),
                    entry.getCreationDate().toLocalDate().toString(),
                    "Load",
                    entry.getId()
            });
        }
    }

    private void addCurrentCommand() {
        DriverCommand currentCommand = commandManager.getCurrentCommand();
        if (currentCommand == null) {
            JOptionPane.showMessageDialog(this, "No current command to add.");
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Enter command name:");
        if (name != null && !name.trim().isEmpty()) {
            catalog.addCommand(name, currentCommand.copy());
            refreshTable();
        }
    }

    private void showCurrentCommand() {
        DriverCommand current = commandManager.getCurrentCommand();
        if (current != null) {
            for (ICommandEntry entry : catalog.getAllEntries()) {
                if (entry.getCommand() == current) {
                    break;
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Currenty loaded command:\n" + current + "\n",
                    "Current Command Info",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No command loaded in Command Manager",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void importCatalog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Command Catalog");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Catalog files (" + catalogStorage.getFileExtension() + ")",
                getFileExtensions()));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                CommandCatalog importedCatalog = catalogStorage.load(file);

                String[] options = {"Merge", "Replace", "Cancel"};
                int choice = JOptionPane.showOptionDialog(this,
                        "Choose how to import:\n" +
                                "Merge - add imported commands to existing catalog\n" +
                                "Replace - clear existing catalog and load imported",
                        "Import Options",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (choice == 0) { // Merge
                    for (ICommandEntry entry : importedCatalog.getAllEntries()) {
                        catalog.addCommand(entry);
                    }
                    JOptionPane.showMessageDialog(this,
                            "Imported " + importedCatalog.size() + " commands (merged).");

                } else if (choice == 1) { // Replace
                    for (ICommandEntry entry : catalog.getAllEntries()) {
                        catalog.removeCommand(entry.getId());
                    }
                    for (ICommandEntry entry : importedCatalog.getAllEntries()) {
                        catalog.addCommand(entry);
                    }
                    JOptionPane.showMessageDialog(this,
                            "Imported " + importedCatalog.size() + " commands (replaced).");
                }

                refreshTable();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error importing catalog:\n" + ex.getMessage(),
                        "Import Error",
                        JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, "Error importing catalog", ex);
            }
        }
    }

    private void exportCatalog() {
        // Ensures currently edited table cell is committed before serialization.
        if (catalogTable != null && catalogTable.isEditing()) {
            TableCellEditor editor = catalogTable.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }

        if (catalog.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Catalog is empty. Nothing to export.",
                    "Export Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Command Catalog");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Catalog files (" + catalogStorage.getFileExtension() + ")",
                getFileExtensions()));


        String defaultExtension = getPrimaryFileExtension();
        fileChooser.setSelectedFile(new File("command_catalog." + defaultExtension));

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + "." + defaultExtension);
            }

            if (file.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(this,
                        "File already exists. Overwrite?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION);
                if (overwrite != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {
                catalogStorage.save(catalog, file);
                JOptionPane.showMessageDialog(this,
                        "Catalog exported successfully to:\n" + file.getAbsolutePath(),
                        "Export Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting catalog:\n" + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, "Error exporting catalog", ex);
            }
        }
    }

    private String[] getFileExtensions() {
        return Arrays.stream(catalogStorage.getFileExtension().split(";"))
                .map(String::trim)
                .map(ext -> ext.replace("*.", ""))
                .filter(ext -> !ext.isEmpty())
                .toArray(String[]::new);
    }

    private String getPrimaryFileExtension() {
        String[] extensions = getFileExtensions();
        if (extensions.length == 0) {
            return "properties";
        }
        return extensions[0];
    }

    @Override
    public void update() {
        SwingUtilities.invokeLater(() -> {
            refreshTable();

            DriverCommand currentCommand = commandManager.getCurrentCommand();
            if (currentCommand == null) {
                catalogTable.clearSelection();
            } else {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String id = (String) tableModel.getValueAt(i, 5);
                    Optional<ICommandEntry> entry = catalog.getEntry(id);
                    if (entry.isPresent() && entry.get().getCommand() == currentCommand) {
                        catalogTable.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void HideIfVisibleAndShowIfHidden() {
        setVisible(!isVisible());
    }

    @Override
    public void setVisible(boolean visible) {
        boolean wasVisible = isVisible();
        super.setVisible(visible);
        if (visible && !wasVisible) {
            logger.info("CommandCatalogWindow started");
        }
    }


    static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(220, 220, 255));
            setBorder(BorderFactory.createRaisedBevelBorder());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Load");

            if (isSelected) {
                setBackground(new Color(180, 180, 230));
            } else {
                setBackground(new Color(220, 220, 255));
            }
            return this;
        }
    }




}