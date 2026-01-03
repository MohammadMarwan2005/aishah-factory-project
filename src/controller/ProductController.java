package controller;

import model.inventroy.ProductInventory;
import model.inventroy.TaskInventory;
import model.io.ErrorLogger;
import model.structure.Product;
import model.structure.Task;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Product View (Swing version with JTable).
 * Displays products in a table with tasks, edit, and delete buttons in each row.
 */
public class ProductController extends JFrame {

    // ===== UI Components =====
    private ProductTableModel tableModel;
    private JTable productTable;
    private JTextField nameField;
    private JTextArea requiredItemsArea;
    private JTextField estimatedTimeField;
    private JLabel statusLabel;
    private JLabel formTitleLabel;
    private JButton addButton;
    private JButton updateButton;
    private JButton cancelButton;

    // ===== Edit Mode State =====
    private Product editingProduct = null;

    public ProductController() {
        setTitle("Product Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 500);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        loadProducts();
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createTablePanel(), BorderLayout.CENTER);
        panel.add(createFormPanel(), BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JLabel label = new JLabel("Products List");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

        tableModel = new ProductTableModel();
        productTable = new JTable(tableModel);
        productTable.setRowHeight(35);
        productTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Center align data columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 4; i++) {
            productTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set up the tasks button column
        productTable.getColumnModel().getColumn(4).setCellRenderer(new TasksButtonRenderer());
        productTable.getColumnModel().getColumn(4).setCellEditor(new TasksButtonEditor());
        productTable.getColumnModel().getColumn(4).setPreferredWidth(70);

        // Set up the edit button column
        productTable.getColumnModel().getColumn(5).setCellRenderer(new EditButtonRenderer());
        productTable.getColumnModel().getColumn(5).setCellEditor(new EditButtonEditor());
        productTable.getColumnModel().getColumn(5).setPreferredWidth(60);

        // Set up the delete button column
        productTable.getColumnModel().getColumn(6).setCellRenderer(new DeleteButtonRenderer());
        productTable.getColumnModel().getColumn(6).setCellEditor(new DeleteButtonEditor());
        productTable.getColumnModel().getColumn(6).setPreferredWidth(60);

        // Set column widths
        productTable.getColumnModel().getColumn(0).setPreferredWidth(40);   // ID
        productTable.getColumnModel().getColumn(1).setPreferredWidth(130);  // Name
        productTable.getColumnModel().getColumn(2).setPreferredWidth(160);  // Required Items
        productTable.getColumnModel().getColumn(3).setPreferredWidth(80);   // Est. Time

        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(250, 0));

        formTitleLabel = new JLabel("Add New Product");
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

        // Required Items area
        panel.add(createLabel("Required Items:"));
        panel.add(createLabel("(Format: itemId:qty, ...)"));
        requiredItemsArea = new JTextArea(4, 20);
        requiredItemsArea.setFont(new Font("Arial", Font.PLAIN, 12));
        requiredItemsArea.setLineWrap(true);
        requiredItemsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(requiredItemsArea);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(10));

        // Estimated Production Time
        panel.add(createLabel("Est. Time (seconds):"));
        estimatedTimeField = new JTextField(20);
        estimatedTimeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(estimatedTimeField);
        panel.add(Box.createVerticalStrut(15));

        // Add button
        addButton = new JButton("Add Product");
        addButton.setBackground(new Color(39, 174, 96));
        addButton.setForeground(Color.WHITE);
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.addActionListener(e -> onAddProduct());
        panel.add(addButton);

        panel.add(Box.createVerticalStrut(8));

        // Update button (hidden by default)
        updateButton = new JButton("Update Product");
        updateButton.setBackground(new Color(52, 152, 219));
        updateButton.setForeground(Color.WHITE);
        updateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateButton.addActionListener(e -> onUpdateProduct());
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

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(236, 240, 241));

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.DARK_GRAY);
        panel.add(statusLabel);

        return panel;
    }

    private void loadProducts() {
        List<Product> products = ProductInventory.getAllProducts();
        tableModel.setProducts(products);
    }

    private void startEdit(int rowIndex) {
        editingProduct = tableModel.getProductAt(rowIndex);

        nameField.setText(editingProduct.getName());
        requiredItemsArea.setText(mapToString(editingProduct.getRequiredItems()));
        estimatedTimeField.setText(String.valueOf(editingProduct.getEstimatedProductionTimeSeconds()));

        formTitleLabel.setText("Edit Product (ID: " + editingProduct.getId() + ")");
        addButton.setVisible(false);
        updateButton.setVisible(true);
        cancelButton.setVisible(true);

        statusLabel.setText("Editing product: " + editingProduct.getName());
    }

    private void cancelEdit() {
        editingProduct = null;
        clearFields();

        formTitleLabel.setText("Add New Product");
        addButton.setVisible(true);
        updateButton.setVisible(false);
        cancelButton.setVisible(false);

        statusLabel.setText("Edit cancelled");
    }

    private void onAddProduct() {
        try {
            String name = nameField.getText().trim();
            Map<Integer, Integer> requiredItems = parseRequiredItems(requiredItemsArea.getText());
            int estimatedTime = Integer.parseInt(estimatedTimeField.getText().trim());

            if (name.isEmpty()) {
                statusLabel.setText("Error: Name cannot be empty");
                return;
            }

            Product newProduct = new Product(name, requiredItems, estimatedTime);
            ProductInventory.addProduct(newProduct);
            tableModel.addProduct(newProduct);
            clearFields();

            statusLabel.setText("Product added: " + newProduct.getName() + " (ID: " + newProduct.getId() + ")");

        } catch (NumberFormatException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error: Invalid number format");
        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error saving product: " + e.getMessage());
        }
    }

    private void onUpdateProduct() {
        if (editingProduct == null) return;

        try {
            String name = nameField.getText().trim();
            Map<Integer, Integer> requiredItems = parseRequiredItems(requiredItemsArea.getText());
            int estimatedTime = Integer.parseInt(estimatedTimeField.getText().trim());

            if (name.isEmpty()) {
                statusLabel.setText("Error: Name cannot be empty");
                return;
            }

            Product updatedProduct = new Product(editingProduct.getId(), name, requiredItems, estimatedTime);
            ProductInventory.updateProduct(updatedProduct);
            tableModel.updateProduct(editingProduct, updatedProduct);
            cancelEdit();

            statusLabel.setText("Product updated: " + updatedProduct.getName() + " (ID: " + updatedProduct.getId() + ")");

        } catch (NumberFormatException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error: Invalid number format");
        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error updating product: " + e.getMessage());
        }
    }

    private void deleteProduct(int rowIndex) {
        Product product = tableModel.getProductAt(rowIndex);

        if (editingProduct != null && editingProduct.getId() == product.getId()) {
            cancelEdit();
        }

        try {
            ProductInventory.deleteProduct(product.getId());
            tableModel.removeProduct(rowIndex);
            statusLabel.setText("Product deleted: " + product.getName());

        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error deleting product: " + e.getMessage());
        }
    }

    private void openTasks(int rowIndex) {
        Product product = tableModel.getProductAt(rowIndex);
        List<Task> tasks = TaskInventory.getProductTasks(product.getId());
        TasksTemplateController tasksController = new TasksTemplateController(tasks);
        tasksController.setVisible(true);
    }

    private void clearFields() {
        nameField.setText("");
        requiredItemsArea.setText("");
        estimatedTimeField.setText("");
    }

    private Map<Integer, Integer> parseRequiredItems(String text) {
        Map<Integer, Integer> result = new HashMap<>();
        if (text == null || text.trim().isEmpty()) {
            return result;
        }

        String[] pairs = text.split(",");
        for (String pair : pairs) {
            String[] parts = pair.trim().split(":");
            if (parts.length == 2) {
                int itemId = Integer.parseInt(parts[0].trim());
                int quantity = Integer.parseInt(parts[1].trim());
                result.put(itemId, quantity);
            }
        }
        return result;
    }

    private String mapToString(Map<Integer, Integer> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append(":").append(entry.getValue());
            first = false;
        }
        return sb.toString();
    }

    private String formatTime(int seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            int mins = seconds / 60;
            int secs = seconds % 60;
            return mins + "m " + secs + "s";
        } else {
            int hours = seconds / 3600;
            int mins = (seconds % 3600) / 60;
            return hours + "h " + mins + "m";
        }
    }

    // =========================================================================
    // INNER CLASS: Table Model for Products
    // =========================================================================
    private class ProductTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Name", "Required Items", "Est. Time", "Tasks", "Edit", "Delete"};
        private List<Product> products = new ArrayList<>();

        public void setProducts(List<Product> products) {
            this.products = new ArrayList<>(products);
            fireTableDataChanged();
        }

        public void addProduct(Product product) {
            products.add(product);
            fireTableRowsInserted(products.size() - 1, products.size() - 1);
        }

        public void removeProduct(int rowIndex) {
            products.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }

        public void updateProduct(Product oldProduct, Product newProduct) {
            int index = products.indexOf(oldProduct);
            if (index >= 0) {
                products.set(index, newProduct);
                fireTableRowsUpdated(index, index);
            }
        }

        public Product getProductAt(int rowIndex) {
            return products.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return products.size();
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
            Product product = products.get(rowIndex);
            switch (columnIndex) {
                case 0: return product.getId();
                case 1: return product.getName();
                case 2: return mapToString(product.getRequiredItems());
                case 3: return formatTime(product.getEstimatedProductionTimeSeconds());
                case 4: return "📋"; // Tasks icon
                case 5: return "✏"; // Edit icon
                case 6: return "🗑"; // Delete icon
                default: return null;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex >= 4; // Tasks, Edit, Delete columns are clickable
        }
    }

    // =========================================================================
    // INNER CLASS: Renderer for Tasks Button
    // =========================================================================
    private class TasksButtonRenderer extends JButton implements TableCellRenderer {
        public TasksButtonRenderer() {
            setText("📋");
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            setBackground(new Color(155, 89, 182));
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
    // INNER CLASS: Editor for Tasks Button (handles click)
    // =========================================================================
    private class TasksButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public TasksButtonEditor() {
            super(new JCheckBox());

            button = new JButton("📋");
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            button.setBackground(new Color(155, 89, 182));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);

            button.addActionListener(e -> {
                fireEditingStopped();
                openTasks(currentRow);
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
            return "📋";
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
            super(new JCheckBox());

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
            super(new JCheckBox());

            button = new JButton("🗑");
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            button.setBackground(new Color(231, 76, 60));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);

            button.addActionListener(e -> {
                fireEditingStopped();
                deleteProduct(currentRow);
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
