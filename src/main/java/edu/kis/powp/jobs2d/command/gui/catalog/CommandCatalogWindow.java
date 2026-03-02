
package edu.kis.powp.jobs2d.command.gui.catalog;

import edu.kis.powp.appbase.gui.WindowComponent;
import edu.kis.powp.jobs2d.command.catalog.CommandCatalog;
import edu.kis.powp.jobs2d.command.catalog.CommandCatalogEntry;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.observer.Subscriber;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.catalog.CommandCatalogIO;
import java.awt.event.ActionEvent;
import edu.kis.powp.jobs2d.drivers.DriverManager;
import edu.kis.powp.jobs2d.features.DriverFeature;


public class CommandCatalogWindow extends JFrame implements WindowComponent, Subscriber {
    private final CommandCatalog catalog;
    private final CommandManager commandManager;
    private JTable catalogTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;

    private JButton btnRunCommand;
    private ActionListener runCommandActionListener;



    public CommandCatalogWindow(CommandCatalog catalog, CommandManager commandManager) {
        this.catalog = catalog;
        this.commandManager = commandManager;
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

        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearSearch());
        searchPanel.add(btnClear);

        btnRunCommand = new JButton("Run Command");
        btnRunCommand.addActionListener(e -> runSelectedCommand());
        btnRunCommand.setEnabled(false);
        searchPanel.add(btnRunCommand);


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
                List<CommandCatalogEntry> entries = catalog.findByName(name);

                if (!entries.isEmpty()) {
                    CommandCatalogEntry entry = entries.get(0);
                    if (column == 0) { // Name column
                        String newName = (String) value;
                        entry.setName(newName);
                        System.out.println("Name updated for " + name + ": " + newName);
                    } else if (column == 1) { // Description column
                        String newDescription = (String) value;
                        entry.setDescription(newDescription);
                        System.out.println("Description updated for " + name + ": " + newDescription);
                    } else if (column == 2) { // Tags column
                        entry.setTags((String) value);
                        System.out.println("Tags updated for " + name + ": " + value);
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
                    List<CommandCatalogEntry> entries = catalog.findByName(name);
                    if (!entries.isEmpty()) {

                        btnRunCommand.setEnabled(true);
                        commandManager.setCurrentCommand(entries.get(0).getCommand());


                        JOptionPane.showMessageDialog(CommandCatalogWindow.this,
                                "Command loaded: " + name);

                        catalogTable.setRowSelectionInterval(row, row);
                    }
                }
            }
        });




    }

    public void setRunCommandActionListener(ActionListener listener) {
        this.runCommandActionListener = listener;
    }

    private void runSelectedCommand() {
        int selectedRow = catalogTable.getSelectedRow();

        DriverCommand currentCommand = commandManager.getCurrentCommand();
        if (currentCommand == null) {
            JOptionPane.showMessageDialog(this,
                    "No command loaded. Please load a command first using the Load button.",
                    "No Command Loaded",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String commandName = (String) tableModel.getValueAt(selectedRow, 0);
        List<CommandCatalogEntry> entries = catalog.findByName(commandName);

        try {
            if (runCommandActionListener != null) {
                runCommandActionListener.actionPerformed(
                        new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "RunCommand")
                );
            } else {
                DriverManager driverManager = DriverFeature.getDriverManager();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error running command: " + ex.getMessage(),
                    "Execution Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void performSearch() {
        String query = searchField.getText().trim();
        String searchType = (String) searchTypeCombo.getSelectedItem();

        List<CommandCatalogEntry> results;

        if (query.isEmpty()) {
            results = catalog.getAllEntries();
        } else {
            switch (Objects.requireNonNull(searchType)) {
                case "Name":
                    results = catalog.findByName(query);
                    break;
                case "Tags":
                    String[] tagsArray = query.split(",");
                    List<String> tags = new java.util.ArrayList<>();
                    for (String tag : tagsArray) {
                        String trimmed = tag.trim();
                        if (!trimmed.isEmpty()) {
                            tags.add(trimmed);
                        }
                    }
                    results = catalog.findByTags(tags);
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
        List<CommandCatalogEntry> allEntries = catalog.getAllEntries();
        updateTable(allEntries);
    }

    private void updateTable(List<CommandCatalogEntry> entries) {
        tableModel.setRowCount(0);
        for (CommandCatalogEntry entry : entries) {
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
            String catalogName = "Not in catalog";
            for (CommandCatalogEntry entry : catalog.getAllEntries()) {
                if (entry.getCommand() == current) {
                    catalogName = entry.getName();
                    break;
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Current command in Manager:\n" + current + "\n\n" +
                            "In Catalog as Name: " + catalogName,
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
                "Catalog files (*.properties)", "properties"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                CommandCatalog importedCatalog = CommandCatalogIO.loadFromProperties(file);

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
                    for (CommandCatalogEntry entry : importedCatalog.getAllEntries()) {
                        catalog.addCommand(entry);
                    }
                    JOptionPane.showMessageDialog(this,
                            "Imported " + importedCatalog.size() + " commands (merged).");

                } else if (choice == 1) { // Replace
                    for (CommandCatalogEntry entry : catalog.getAllEntries()) {
                        catalog.removeCommand(entry.getId());
                    }
                    for (CommandCatalogEntry entry : importedCatalog.getAllEntries()) {
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
                ex.printStackTrace();
            }
        }
    }

    private void exportCatalog() {
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
                "Catalog files (*.properties)", "properties"));


        fileChooser.setSelectedFile(new File("command_catalog.properties"));

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".properties");
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
                CommandCatalogIO.saveToProperties(catalog, file);
                JOptionPane.showMessageDialog(this,
                        "Catalog exported successfully to:\n" + file.getAbsolutePath(),
                        "Export Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting catalog:\n" + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void update() {
        SwingUtilities.invokeLater(() -> {
            refreshTable();

            DriverCommand currentCommand = commandManager.getCurrentCommand();
            if (currentCommand == null) {
                btnRunCommand.setEnabled(false);

                catalogTable.clearSelection();
            } else {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String id = (String) tableModel.getValueAt(i, 5);
                    CommandCatalogEntry entry = catalog.getEntry(id);
                    if (entry != null && entry.getCommand() == currentCommand) {
                        catalogTable.setRowSelectionInterval(i, i);
                        btnRunCommand.setEnabled(true);
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


    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
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