package controller;

import model.inventroy.ItemInventory;
import model.io.ErrorLogger;
import model.structure.Category;
import model.structure.Item;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Item View (Swing version with JTable).
 * Displays items in a table with edit and delete buttons in each row.
 */
public class ItemController extends JFrame {

    // ===== UI Components =====
    private ItemTableModel tableModel;
    private JTable itemTable;
    private JTextField nameField;
    private JComboBox<Category> categoryComboBox;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextField minQuantityField;
    private JLabel statusLabel;
    private JLabel formTitleLabel;
    private JButton addButton;
    private JButton updateButton;
    private JButton cancelButton;

    // ===== Edit Mode State =====
    private Item editingItem = null;

    /**
     * Constructor - builds the entire UI.
     */
    public ItemController() {
        setTitle("Factory Inventory System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null); // Center on screen

        // Main layout
        setLayout(new BorderLayout(10, 10));

        // Add components
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        // Load initial data
        loadItems();
    }

    /**
     * Creates the title panel at the top.
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Item Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel);

        return panel;
    }

    /**
     * Creates the main panel with table and form.
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: Items table
        panel.add(createTablePanel(), BorderLayout.CENTER);

        // Right: Add/Edit form
        panel.add(createFormPanel(), BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates the table panel with items.
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Title
        JLabel label = new JLabel("Items List");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

        // Table
        tableModel = new ItemTableModel();
        itemTable = new JTable(tableModel);
        itemTable.setRowHeight(35);
        itemTable.setFont(new Font("Arial", Font.PLAIN, 14));
        itemTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Center align data columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 6; i++) {
            itemTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set up the edit button column
        itemTable.getColumnModel().getColumn(6).setCellRenderer(new EditButtonRenderer());
        itemTable.getColumnModel().getColumn(6).setCellEditor(new EditButtonEditor());
        itemTable.getColumnModel().getColumn(6).setPreferredWidth(60);

        // Set up the delete button column
        itemTable.getColumnModel().getColumn(7).setCellRenderer(new DeleteButtonRenderer());
        itemTable.getColumnModel().getColumn(7).setCellEditor(new DeleteButtonEditor());
        itemTable.getColumnModel().getColumn(7).setPreferredWidth(60);

        // Set column widths
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        itemTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Name
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Category
        itemTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Price
        itemTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Quantity
        itemTable.getColumnModel().getColumn(5).setPreferredWidth(70);  // Min Qty

        JScrollPane scrollPane = new JScrollPane(itemTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the right panel with the add/edit item form.
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(220, 0));

        // Title
        formTitleLabel = new JLabel("Add New Item");
        formTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(formTitleLabel);
        panel.add(Box.createVerticalStrut(15));

        // Name field
        panel.add(createLabel("Name:"));
        nameField = new JTextField(20);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));

        // Category dropdown
        panel.add(createLabel("Category:"));
        categoryComboBox = new JComboBox<>(Category.values());
        categoryComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(categoryComboBox);
        panel.add(Box.createVerticalStrut(10));

        // Price field
        panel.add(createLabel("Price:"));
        priceField = new JTextField(20);
        priceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(priceField);
        panel.add(Box.createVerticalStrut(10));

        // Quantity field
        panel.add(createLabel("Quantity:"));
        quantityField = new JTextField(20);
        quantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(quantityField);
        panel.add(Box.createVerticalStrut(10));

        // Min Quantity field
        panel.add(createLabel("Min Quantity:"));
        minQuantityField = new JTextField(20);
        minQuantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(minQuantityField);
        panel.add(Box.createVerticalStrut(15));

        // Add button
        addButton = new JButton("Add Item");
        addButton.setBackground(new Color(39, 174, 96));
        addButton.setForeground(Color.WHITE);
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.addActionListener(e -> onAddItem());
        panel.add(addButton);

        panel.add(Box.createVerticalStrut(8));

        // Update button (hidden by default)
        updateButton = new JButton("Update Item");
        updateButton.setBackground(new Color(52, 152, 219));
        updateButton.setForeground(Color.WHITE);
        updateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateButton.addActionListener(e -> onUpdateItem());
        updateButton.setVisible(false);
        panel.add(updateButton);

        panel.add(Box.createVerticalStrut(8));

        // Cancel button (hidden by default)
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelButton.addActionListener(e -> cancelEdit());
        cancelButton.setVisible(false);
        panel.add(cancelButton);

        return panel;
    }

    /**
     * Creates a simple label.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * Creates the status bar at the bottom.
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(236, 240, 241));

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.DARK_GRAY);
        panel.add(statusLabel);

        return panel;
    }

    /**
     * Loads all items from files into the table.
     */
    private void loadItems() {
        List<Item> items = ItemInventory.getAllItems();
        tableModel.setItems(items);
    }

    /**
     * Switches to edit mode for the given item.
     */
    private void startEdit(int rowIndex) {
        editingItem = tableModel.getItemAt(rowIndex);

        // Populate form with item data
        nameField.setText(editingItem.getName());
        categoryComboBox.setSelectedItem(editingItem.getCategory());
        priceField.setText(String.valueOf(editingItem.getPrice()));
        quantityField.setText(String.valueOf(editingItem.getQuantity()));
        minQuantityField.setText(String.valueOf(editingItem.getMinQuantity()));

        // Update UI for edit mode
        formTitleLabel.setText("Edit Item (ID: " + editingItem.getId() + ")");
        addButton.setVisible(false);
        updateButton.setVisible(true);
        cancelButton.setVisible(true);

        statusLabel.setText("Editing item: " + editingItem.getName());
    }

    /**
     * Cancels edit mode and returns to add mode.
     */
    private void cancelEdit() {
        editingItem = null;
        clearFields();

        // Update UI for add mode
        formTitleLabel.setText("Add New Item");
        addButton.setVisible(true);
        updateButton.setVisible(false);
        cancelButton.setVisible(false);

        statusLabel.setText("Edit cancelled");
    }

    /**
     * Handles the "Add Item" button click.
     */
    private void onAddItem() {
        try {
            // Get values from fields
            String name = nameField.getText().trim();
            Category category = (Category) categoryComboBox.getSelectedItem();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int minQuantity = Integer.parseInt(minQuantityField.getText().trim());

            // Validate
            if (name.isEmpty()) {
                statusLabel.setText("Error: Name cannot be empty");
                return;
            }

            // Create new item (ID is auto-generated!)
            Item newItem = new Item(name, category, price, quantity, minQuantity);

            // Save to file
            ItemInventory.addItem(newItem);

            // Add to table
            tableModel.addItem(newItem);

            // Clear fields
            clearFields();

            statusLabel.setText("Item added: " + newItem.getName() + " (ID: " + newItem.getId() + ")");

        } catch (NumberFormatException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error: Please enter valid numbers");
        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error saving item: " + e.getMessage());
        }
    }

    /**
     * Handles the "Update Item" button click.
     */
    private void onUpdateItem() {
        if (editingItem == null) return;

        try {
            // Get values from fields
            String name = nameField.getText().trim();
            Category category = (Category) categoryComboBox.getSelectedItem();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int minQuantity = Integer.parseInt(minQuantityField.getText().trim());

            // Validate
            if (name.isEmpty()) {
                statusLabel.setText("Error: Name cannot be empty");
                return;
            }

            // Create updated item with same ID
            Item updatedItem = new Item(editingItem.getId(), name, category, price, quantity, minQuantity);

            // Save to file (overwrites existing)
            ItemInventory.updateItem(updatedItem);

            // Update in table
            tableModel.updateItem(editingItem, updatedItem);

            // Exit edit mode
            cancelEdit();

            statusLabel.setText("Item updated: " + updatedItem.getName() + " (ID: " + updatedItem.getId() + ")");

        } catch (NumberFormatException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error: Please enter valid numbers");
        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error updating item: " + e.getMessage());
        }
    }

    /**
     * Deletes an item by row index.
     */
    private void deleteItem(int rowIndex) {
        Item item = tableModel.getItemAt(rowIndex);

        // If we're editing this item, cancel edit first
        if (editingItem != null && editingItem.getId() == item.getId()) {
            cancelEdit();
        }

        try {
            // Delete from file
            ItemInventory.deleteItem(item.getId());

            // Remove from table
            tableModel.removeItem(rowIndex);

            statusLabel.setText("Item deleted: " + item.getName());

        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error deleting item: " + e.getMessage());
        }
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
        minQuantityField.setText("");
        categoryComboBox.setSelectedIndex(0);
    }

    // =========================================================================
    // INNER CLASS: Table Model for Items
    // =========================================================================
    private class ItemTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Name", "Category", "Price", "Qty", "Min Qty", "Edit", "Delete"};
        private List<Item> items = new ArrayList<>();

        public void setItems(List<Item> items) {
            this.items = new ArrayList<>(items);
            fireTableDataChanged();
        }

        public void addItem(Item item) {
            items.add(item);
            fireTableRowsInserted(items.size() - 1, items.size() - 1);
        }

        public void removeItem(int rowIndex) {
            items.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }

        public void updateItem(Item oldItem, Item newItem) {
            int index = items.indexOf(oldItem);
            if (index >= 0) {
                items.set(index, newItem);
                fireTableRowsUpdated(index, index);
            }
        }

        public Item getItemAt(int rowIndex) {
            return items.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return items.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Item item = items.get(rowIndex);
            switch (columnIndex) {
                case 0: return item.getId();
                case 1: return item.getName();
                case 2: return item.getCategory();
                case 3: return item.getPrice();
                case 4: return item.getQuantity();
                case 5: return item.getMinQuantity();
                case 6: return "✏"; // Edit icon
                case 7: return "🗑"; // Delete icon
                default: return null;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 6 || columnIndex == 7; // Edit and Delete columns are clickable
        }
    }

    // =========================================================================
    // INNER CLASS: Renderer for Edit Button
    // =========================================================================
    private class EditButtonRenderer extends JButton implements TableCellRenderer {
        public EditButtonRenderer() {
            setText("✏");
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            setBackground(new Color(52, 152, 219));
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // =========================================================================
    // INNER CLASS: Editor for Edit Button (handles click)
    // =========================================================================
    private class EditButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public EditButtonEditor() {
            super(new JCheckBox()); // Required by DefaultCellEditor

            button = new JButton("✏");
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            button.setBackground(new Color(52, 152, 219));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);

            button.addActionListener(e -> {
                fireEditingStopped();
                startEdit(currentRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "✏";
        }
    }

    // =========================================================================
    // INNER CLASS: Renderer for Delete Button
    // =========================================================================
    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setText("🗑");
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            setBackground(new Color(231, 76, 60));
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // =========================================================================
    // INNER CLASS: Editor for Delete Button (handles click)
    // =========================================================================
    private class DeleteButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public DeleteButtonEditor() {
            super(new JCheckBox()); // Required by DefaultCellEditor

            button = new JButton("🗑");
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            button.setBackground(new Color(231, 76, 60));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);

            button.addActionListener(e -> {
                fireEditingStopped();
                deleteItem(currentRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "🗑";
        }
    }
}
