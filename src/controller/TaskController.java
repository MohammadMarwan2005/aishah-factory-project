package controller;

import model.inventroy.TaskInventory;
import model.io.ErrorLogger;
import model.structure.Task;
import model.structure.TaskStatus;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TaskController extends JFrame {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ===== UI Components =====
    private TaskTableModel tableModel;
    private JTable taskTable;
    private JTextField productIdField;
    private JTextField productLineIdField;
    private JTextField quantityField;
    private JTextField completedQuantityField;
    private JTextField clientNameField;
    private JTextField startDateField;
    private JTextField finishDateField;
    private JComboBox<TaskStatus> statusComboBox;
    private JLabel statusLabel;
    private JLabel formTitleLabel;
    private JButton addButton;
    private JButton updateButton;
    private JButton cancelButton;

    // ===== Edit Mode State =====
    private Task editingTask = null;

    public TaskController() {
        setTitle("Task Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        loadTasks();
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(155, 89, 182));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Task Management");
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

        JLabel label = new JLabel("Tasks List");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

        tableModel = new TaskTableModel();
        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(35);
        taskTable.setFont(new Font("Arial", Font.PLAIN, 13));
        taskTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Center align data columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 10; i++) {
            taskTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Progress column with custom renderer
        taskTable.getColumnModel().getColumn(8).setCellRenderer(new ProgressBarRenderer());

        // Set up the edit button column
        taskTable.getColumnModel().getColumn(10).setCellRenderer(new EditButtonRenderer());
        taskTable.getColumnModel().getColumn(10).setCellEditor(new EditButtonEditor());
        taskTable.getColumnModel().getColumn(10).setPreferredWidth(50);

        // Set up the delete button column
        taskTable.getColumnModel().getColumn(11).setCellRenderer(new DeleteButtonRenderer());
        taskTable.getColumnModel().getColumn(11).setCellEditor(new DeleteButtonEditor());
        taskTable.getColumnModel().getColumn(11).setPreferredWidth(50);

        // Set column widths
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(35);   // ID
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(55);   // Product
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(45);   // Line
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(40);   // Qty
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(45);   // Done
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(50);   // Client
        taskTable.getColumnModel().getColumn(6).setPreferredWidth(80);   // Start
        taskTable.getColumnModel().getColumn(7).setPreferredWidth(80);   // Finish
        taskTable.getColumnModel().getColumn(8).setPreferredWidth(90);   // Progress
        taskTable.getColumnModel().getColumn(9).setPreferredWidth(75);   // Status

        JScrollPane scrollPane = new JScrollPane(taskTable);
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
        panel.setPreferredSize(new Dimension(200, 0));

        formTitleLabel = new JLabel("Add New Task");
        formTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(formTitleLabel);
        panel.add(Box.createVerticalStrut(12));

        // Product ID (optional)
        panel.add(createLabel("Product ID (optional):"));
        productIdField = new JTextField(15);
        productIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(productIdField);
        panel.add(Box.createVerticalStrut(8));

        // Product Line ID (optional)
        panel.add(createLabel("Line ID (optional):"));
        productLineIdField = new JTextField(15);
        productLineIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(productLineIdField);
        panel.add(Box.createVerticalStrut(8));

        // Quantity
        panel.add(createLabel("Quantity:"));
        quantityField = new JTextField(15);
        quantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(quantityField);
        panel.add(Box.createVerticalStrut(8));

        // Completed Quantity
        panel.add(createLabel("Completed:"));
        completedQuantityField = new JTextField(15);
        completedQuantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(completedQuantityField);
        panel.add(Box.createVerticalStrut(8));

        // Client Name
        panel.add(createLabel("Client Name:"));
        clientNameField = new JTextField(15);
        clientNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(clientNameField);
        panel.add(Box.createVerticalStrut(8));

        // Start Date
        panel.add(createLabel("Start (yyyy-MM-dd):"));
        startDateField = new JTextField(15);
        startDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(startDateField);
        panel.add(Box.createVerticalStrut(8));

        // Finish Date
        panel.add(createLabel("Finish (yyyy-MM-dd):"));
        finishDateField = new JTextField(15);
        finishDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(finishDateField);
        panel.add(Box.createVerticalStrut(8));

        // Status
        panel.add(createLabel("Status:"));
        statusComboBox = new JComboBox<>(TaskStatus.values());
        statusComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(statusComboBox);
        panel.add(Box.createVerticalStrut(12));

        // Add button
        addButton = new JButton("Add Task");
        addButton.setBackground(new Color(39, 174, 96));
        addButton.setForeground(Color.WHITE);
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.addActionListener(e -> onAddTask());
        panel.add(addButton);

        panel.add(Box.createVerticalStrut(6));

        // Update button
        updateButton = new JButton("Update Task");
        updateButton.setBackground(new Color(52, 152, 219));
        updateButton.setForeground(Color.WHITE);
        updateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateButton.addActionListener(e -> onUpdateTask());
        updateButton.setVisible(false);
        panel.add(updateButton);

        panel.add(Box.createVerticalStrut(6));

        // Cancel button
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
        label.setFont(new Font("Arial", Font.PLAIN, 12));
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

    private void loadTasks() {
        List<Task> tasks = TaskInventory.getAllTasks();
        tableModel.setTasks(tasks);
    }

    private void startEdit(int rowIndex) {
        editingTask = tableModel.getTaskAt(rowIndex);

        productIdField.setText(editingTask.getProductId() != null ? String.valueOf(editingTask.getProductId()) : "");
        productLineIdField.setText(editingTask.getProductLineId() != null ? String.valueOf(editingTask.getProductLineId()) : "");
        quantityField.setText(String.valueOf(editingTask.getQuantity()));
        completedQuantityField.setText(String.valueOf(editingTask.getCompletedQuantity()));
        clientNameField.setText(editingTask.getClientName());
        startDateField.setText(editingTask.getStartDate().format(DATE_FORMAT));
        finishDateField.setText(editingTask.getFinishDate().format(DATE_FORMAT));
        statusComboBox.setSelectedItem(editingTask.getStatus());

        formTitleLabel.setText("Edit Task (ID: " + editingTask.getId() + ")");
        addButton.setVisible(false);
        updateButton.setVisible(true);
        cancelButton.setVisible(true);

        statusLabel.setText("Editing task ID: " + editingTask.getId());
    }

    private void cancelEdit() {
        editingTask = null;
        clearFields();

        formTitleLabel.setText("Add New Task");
        addButton.setVisible(true);
        updateButton.setVisible(false);
        cancelButton.setVisible(false);

        statusLabel.setText("Edit cancelled");
    }

    private void onAddTask() {
        try {
            String productIdText = productIdField.getText().trim();
            String productLineIdText = productLineIdField.getText().trim();
            Integer productId = productIdText.isEmpty() ? null : Integer.parseInt(productIdText);
            Integer productLineId = productLineIdText.isEmpty() ? null : Integer.parseInt(productLineIdText);
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int completedQuantity = Integer.parseInt(completedQuantityField.getText().trim());
            String clientName = clientNameField.getText().trim();
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim(), DATE_FORMAT);
            LocalDate finishDate = LocalDate.parse(finishDateField.getText().trim(), DATE_FORMAT);
            TaskStatus status = (TaskStatus) statusComboBox.getSelectedItem();

            if (clientName.isEmpty()) {
                statusLabel.setText("Error: Client name cannot be empty");
                return;
            }

            Task newTask = new Task(productId, productLineId, quantity, completedQuantity,
                    clientName, startDate, finishDate, status);

            TaskInventory.addTask(newTask);
            tableModel.addTask(newTask);
            clearFields();

            statusLabel.setText("Task added (ID: " + newTask.getId() + ")");

        } catch (NumberFormatException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error: Please enter valid numbers");
        } catch (DateTimeParseException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error: Invalid date format (use yyyy-MM-dd)");
        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error saving task: " + e.getMessage());
        }
    }

    private void onUpdateTask() {
        if (editingTask == null) return;

        try {
            String productIdText = productIdField.getText().trim();
            String productLineIdText = productLineIdField.getText().trim();
            Integer productId = productIdText.isEmpty() ? null : Integer.parseInt(productIdText);
            Integer productLineId = productLineIdText.isEmpty() ? null : Integer.parseInt(productLineIdText);
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int completedQuantity = Integer.parseInt(completedQuantityField.getText().trim());
            String clientName = clientNameField.getText().trim();
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim(), DATE_FORMAT);
            LocalDate finishDate = LocalDate.parse(finishDateField.getText().trim(), DATE_FORMAT);
            TaskStatus status = (TaskStatus) statusComboBox.getSelectedItem();

            if (clientName.isEmpty()) {
                statusLabel.setText("Error: Client name cannot be empty");
                return;
            }

            Task updatedTask = new Task(editingTask.getId(), productId, productLineId, quantity,
                    completedQuantity, clientName, startDate, finishDate, status);

            TaskInventory.updateTask(updatedTask);
            tableModel.updateTask(editingTask, updatedTask);
            cancelEdit();

            statusLabel.setText("Task updated (ID: " + updatedTask.getId() + ")");

        } catch (NumberFormatException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error: Please enter valid numbers");
        } catch (DateTimeParseException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error: Invalid date format (use yyyy-MM-dd)");
        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error updating task: " + e.getMessage());
        }
    }

    private void deleteTask(int rowIndex) {
        Task task = tableModel.getTaskAt(rowIndex);

        if (editingTask != null && editingTask.getId() == task.getId()) {
            cancelEdit();
        }

        try {
            TaskInventory.deleteTask(task.getId());
            tableModel.removeTask(rowIndex);
            statusLabel.setText("Task deleted (ID: " + task.getId() + ")");

        } catch (IOException e) {
            ErrorLogger.logError(e);
            statusLabel.setText("Error deleting task: " + e.getMessage());
        }
    }

    private void clearFields() {
        productIdField.setText("");
        productLineIdField.setText("");
        quantityField.setText("");
        completedQuantityField.setText("");
        clientNameField.setText("");
        startDateField.setText("");
        finishDateField.setText("");
        statusComboBox.setSelectedIndex(0);
    }

    // =========================================================================
    // INNER CLASS: Table Model for Tasks
    // =========================================================================
    private class TaskTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Product", "Line", "Qty", "Done", "Client", "Start", "Finish", "Progress", "Status", "Edit", "Delete"};
        private List<Task> tasks = new ArrayList<>();

        public void setTasks(List<Task> tasks) {
            this.tasks = new ArrayList<>(tasks);
            fireTableDataChanged();
        }

        public void addTask(Task task) {
            tasks.add(task);
            fireTableRowsInserted(tasks.size() - 1, tasks.size() - 1);
        }

        public void removeTask(int rowIndex) {
            tasks.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }

        public void updateTask(Task oldTask, Task newTask) {
            int index = tasks.indexOf(oldTask);
            if (index >= 0) {
                tasks.set(index, newTask);
                fireTableRowsUpdated(index, index);
            }
        }

        public Task getTaskAt(int rowIndex) {
            return tasks.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return tasks.size();
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
            Task task = tasks.get(rowIndex);
            switch (columnIndex) {
                case 0: return task.getId();
                case 1: return task.getProductId() != null ? task.getProductId() : "-";
                case 2: return task.getProductLineId() != null ? task.getProductLineId() : "-";
                case 3: return task.getQuantity();
                case 4: return task.getCompletedQuantity();
                case 5: return task.getClientName();
                case 6: return task.getStartDate();
                case 7: return task.getFinishDate();
                case 8: return task.getProgress();
                case 9: return task.getStatus();
                case 10: return "✏";
                case 11: return "🗑";
                default: return null;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex >= 10;
        }
    }

    // =========================================================================
    // INNER CLASS: Progress Bar Renderer
    // =========================================================================
    private static class ProgressBarRenderer extends JProgressBar implements TableCellRenderer {
        public ProgressBarRenderer() {
            setMinimum(0);
            setMaximum(100);
            setStringPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            int progress = (int) Math.round((Double) value);
            setValue(progress);
            setString(progress + "%");

            if (progress < 30) {
                setForeground(new Color(231, 76, 60));
            } else if (progress < 70) {
                setForeground(new Color(241, 196, 15));
            } else {
                setForeground(new Color(39, 174, 96));
            }

            return this;
        }
    }

    // =========================================================================
    // INNER CLASS: Renderer for Edit Button
    // =========================================================================
    private class EditButtonRenderer extends JButton implements TableCellRenderer {
        public EditButtonRenderer() {
            setText("✏");
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
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
    // INNER CLASS: Editor for Edit Button
    // =========================================================================
    private class EditButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public EditButtonEditor() {
            super(new JCheckBox());
            button = new JButton("✏");
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
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
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
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
    // INNER CLASS: Editor for Delete Button
    // =========================================================================
    private class DeleteButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public DeleteButtonEditor() {
            super(new JCheckBox());
            button = new JButton("🗑");
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            button.setBackground(new Color(231, 76, 60));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                fireEditingStopped();
                deleteTask(currentRow);
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
