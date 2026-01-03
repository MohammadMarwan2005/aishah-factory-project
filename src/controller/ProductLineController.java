package controller;

import model.inventroy.ProductLineInventory;
import model.inventroy.TaskInventory;
import model.io.ErrorLogger;
import model.structure.ProductLine;
import model.structure.ProductLineStatus;
import model.structure.Task;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductLineController extends JFrame {

    // ===== UI Components =====
    private ProductLineTableModel tableModel;
    private JTable productLineTable;
    private JTextField nameField;
    private JComboBox<ProductLineStatus> statusComboBox;
    private JLabel statusLabel;
    private JLabel formTitleLabel;
    private JButton addButton;
    private JButton updateButton;
    private JButton cancelButton;

    // ===== Edit Mode State =====
    private ProductLine editingProductLine = null;

    public ProductLineController() {
        setTitle("Production Lines Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        loadProductLines();
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(231, 76, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Production Lines");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
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

        JLabel label = new JLabel("Production Lines List");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

        tableModel = new ProductLineTableModel();
        productLineTable = new JTable(tableModel);
        productLineTable.setRowHeight(35);
        productLineTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productLineTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Center align data columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 4; i++) {
            productLineTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set up the assign button column
        productLineTable.getColumnModel().getColumn(4).setCellRenderer(new AssignButtonRenderer());
        productLineTable.getColumnModel().getColumn(4).setCellEditor(new AssignButtonEditor());
        productLineTable.getColumnModel().getColumn(4).setPreferredWidth(70);

        // Set up the tasks button column
        productLineTable.getColumnModel().getColumn(5).setCellRenderer(new TasksButtonRenderer());
        productLineTable.getColumnModel().getColumn(5).setCellEditor(new TasksButtonEditor());
        productLineTable.getColumnModel().getColumn(5).setPreferredWidth(70);

        // Set up the edit button column
        productLineTable.getColumnModel().getColumn(6).setCellRenderer(new EditButtonRenderer());
        productLineTable.getColumnModel().getColumn(6).setCellEditor(new EditButtonEditor());
        productLineTable.getColumnModel().getColumn(6).setPreferredWidth(60);

        // Set up the delete button column
        productLineTable.getColumnModel().getColumn(7).setCellRenderer(new DeleteButtonRenderer());
        productLineTable.getColumnModel().getColumn(7).setCellEditor(new DeleteButtonEditor());
        productLineTable.getColumnModel().getColumn(7).setPreferredWidth(60);

        // Set column widths
        productLineTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        productLineTable.getColumnModel().getColumn(1).setPreferredWidth(180);  // Name
        productLineTable.getColumnModel().getColumn(2).setPreferredWidth(90);   // Status
        productLineTable.getColumnModel().getColumn(3).setPreferredWidth(60);   // Tasks Count

        JScrollPane scrollPane = new JScrollPane(productLineTable);
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
        panel.setPreferredSize(new Dimension(220, 0));

        formTitleLabel = new JLabel("Add Production Line");
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

        // Status dropdown
        panel.add(createLabel("Status:"));
        statusComboBox = new JComboBox<>(ProductLineStatus.values());
        statusComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(statusComboBox);
        panel.add(Box.createVerticalStrut(15));

        // Add button
        addButton = new JButton("Add Line");
        addButton.setBackground(new Color(39, 174, 96));
        addButton.setForeground(Color.WHITE);
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.addActionListener(e -> onAddProductLine());
        panel.add(addButton);

        panel.add(Box.createVerticalStrut(8));

        // Update button (hidden by default)
        updateButton = new JButton("Update Line");
        updateButton.setBackground(new Color(52, 152, 219));
        updateButton.setForeground(Color.WHITE);
        updateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateButton.addActionListener(e -> onUpdateProductLine());
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

    private void loadProductLines() {
        List<ProductLine> productLines = ProductLineInventory.getAllProductLines();
        tableModel.setProductLines(productLines);
    }

    private void startEdit(int rowIndex) {
        editingProductLine = tableModel.getProductLineAt(rowIndex);

        nameField.setText(editingProductLine.getName());
        statusComboBox.setSelectedItem(editingProductLine.getStatus());

        formTitleLabel.setText("Edit Line (ID: " + editingProductLine.getId() + ")");
        addButton.setVisible(false);
        updateButton.setVisible(true);
        cancelButton.setVisible(true);

        statusLabel.setText("Editing: " + editingProductLine.getName());
    }

    private void cancelEdit() {
        editingProductLine = null;
        clearFields();

        formTitleLabel.setText("Add Production Line");
        addButton.setVisible(true);
        updateButton.setVisible(false);
        cancelButton.setVisible(false);

        statusLabel.setText("Edit cancelled");
    }

    private void onAddProductLine() {
        try {
            String name = nameField.getText().trim();
            ProductLineStatus status = (ProductLineStatus) statusComboBox.getSelectedItem();

            if (name.isEmpty()) {
                statusLabel.setText("Error: Name cannot be empty");
                return;
            }

            ProductLine newLine = new ProductLine(name, status, new ArrayList<>());
            ProductLineInventory.addProductLine(newLine);
            tableModel.addProductLine(newLine);
            clearFields();

            statusLabel.setText("Production line added: " + newLine.getName() + " (ID: " + newLine.getId() + ")");

        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error saving: " + e.getMessage());
        }
    }

    private void onUpdateProductLine() {
        if (editingProductLine == null) return;

        try {
            String name = nameField.getText().trim();
            ProductLineStatus status = (ProductLineStatus) statusComboBox.getSelectedItem();

            if (name.isEmpty()) {
                statusLabel.setText("Error: Name cannot be empty");
                return;
            }

            ProductLine updatedLine = new ProductLine(editingProductLine.getId(), name, status, editingProductLine.getTasks());
            ProductLineInventory.updateProductLine(updatedLine);
            tableModel.updateProductLine(editingProductLine, updatedLine);
            cancelEdit();

            statusLabel.setText("Updated: " + updatedLine.getName() + " (ID: " + updatedLine.getId() + ")");

        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error updating: " + e.getMessage());
        }
    }

    private void deleteProductLine(int rowIndex) {
        ProductLine line = tableModel.getProductLineAt(rowIndex);

        if (editingProductLine != null && editingProductLine.getId() == line.getId()) {
            cancelEdit();
        }

        try {
            ProductLineInventory.deleteProductLine(line.getId());
            tableModel.removeProductLine(rowIndex);
            statusLabel.setText("Deleted: " + line.getName());

        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error deleting: " + e.getMessage());
        }
    }

    private void openTasks(int rowIndex) {
        ProductLine line = tableModel.getProductLineAt(rowIndex);
        List<Task> tasks = TaskInventory.getProductLineTasks(line.getId());
        TasksTemplateController tasksController = new TasksTemplateController(tasks);
        tasksController.setTitle("Tasks for: " + line.getName());
        tasksController.setVisible(true);
    }

    private void openAssignDialog(int rowIndex) {
        ProductLine line = tableModel.getProductLineAt(rowIndex);

        // Get unassigned tasks (tasks with null productLineId)
        List<Task> allTasks = TaskInventory.getAllTasks();
        List<Task> unassignedTasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.getProductLineId() == null) {
                unassignedTasks.add(task);
            }
        }

        if (unassignedTasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No unassigned tasks available.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog(this, "Assign Tasks to: " + line.getName(), true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Create list with checkboxes
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<JCheckBox> checkBoxes = new ArrayList<>();
        for (Task task : unassignedTasks) {
            JCheckBox checkBox = new JCheckBox("Task #" + task.getId() + " - " + task.getClientName() + " (Qty: " + task.getQuantity() + ")");
            checkBox.setFont(new Font("Arial", Font.PLAIN, 14));
            checkBoxes.add(checkBox);
            listPanel.add(checkBox);
            listPanel.add(Box.createVerticalStrut(5));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton assignBtn = new JButton("Assign Selected");
        assignBtn.setBackground(new Color(39, 174, 96));
        assignBtn.setForeground(Color.WHITE);
        JButton cancelBtn = new JButton("Cancel");

        assignBtn.addActionListener(e -> {
            int assignedCount = 0;
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    Task oldTask = unassignedTasks.get(i);
                    // Create updated task with productLineId set
                    Task updatedTask = new Task(
                            oldTask.getId(),
                            oldTask.getProductId(),
                            line.getId(), // Assign to this production line
                            oldTask.getQuantity(),
                            oldTask.getCompletedQuantity(),
                            oldTask.getClientName(),
                            oldTask.getStartDate(),
                            oldTask.getFinishDate(),
                            oldTask.getStatus()
                    );
                    try {
                        TaskInventory.updateTask(updatedTask);
                        assignedCount++;
                    } catch (IOException ex) {
                        ErrorLogger.logError(ex);
                    }
                }
            }
            dialog.dispose();
            tableModel.fireTableDataChanged(); // Refresh task count
            statusLabel.setText("Assigned " + assignedCount + " task(s) to " + line.getName());
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(assignBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Title
        JLabel titleLabel = new JLabel("Select tasks to assign:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        dialog.setVisible(true);
    }

    private void clearFields() {
        nameField.setText("");
        statusComboBox.setSelectedIndex(0);
    }

    // =========================================================================
    // INNER CLASS: Table Model for Production Lines
    // =========================================================================
    private class ProductLineTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Name", "Status", "Tasks", "Assign", "View", "Edit", "Delete"};
        private List<ProductLine> productLines = new ArrayList<>();

        public void setProductLines(List<ProductLine> productLines) {
            this.productLines = new ArrayList<>(productLines);
            fireTableDataChanged();
        }

        public void addProductLine(ProductLine line) {
            productLines.add(line);
            fireTableRowsInserted(productLines.size() - 1, productLines.size() - 1);
        }

        public void removeProductLine(int rowIndex) {
            productLines.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }

        public void updateProductLine(ProductLine oldLine, ProductLine newLine) {
            int index = productLines.indexOf(oldLine);
            if (index >= 0) {
                productLines.set(index, newLine);
                fireTableRowsUpdated(index, index);
            }
        }

        public ProductLine getProductLineAt(int rowIndex) {
            return productLines.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return productLines.size();
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
            ProductLine line = productLines.get(rowIndex);
            switch (columnIndex) {
                case 0: return line.getId();
                case 1: return line.getName();
                case 2: return line.getStatus();
                case 3: return TaskInventory.getProductLineTasks(line.getId()).size();
                case 4: return "➕"; // Assign icon
                case 5: return "📋"; // Tasks icon
                case 6: return "✏"; // Edit icon
                case 7: return "🗑"; // Delete icon
                default: return null;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex >= 4; // Tasks, Edit, Delete columns are clickable
        }
    }

    // =========================================================================
    // INNER CLASS: Renderer for Assign Button
    // =========================================================================
    private class AssignButtonRenderer extends JButton implements TableCellRenderer {
        public AssignButtonRenderer() {
            setText("➕");
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            setBackground(new Color(46, 204, 113));
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
    // INNER CLASS: Editor for Assign Button (handles click)
    // =========================================================================
    private class AssignButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public AssignButtonEditor() {
            super(new JCheckBox());

            button = new JButton("➕");
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            button.setBackground(new Color(46, 204, 113));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);

            button.addActionListener(e -> {
                fireEditingStopped();
                openAssignDialog(currentRow);
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
            return "➕";
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
                deleteProductLine(currentRow);
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

