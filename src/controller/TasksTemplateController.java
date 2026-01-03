package controller;

import model.inventroy.TaskInventory;
import model.structure.Task;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TasksTemplateController extends JFrame {

    private final List<Task> tasks;
    private TaskTableModel tableModel;

    public TasksTemplateController(List<Task> tasks) {
        this.tasks = tasks;

        setTitle("Tasks Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(155, 89, 182));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Tasks");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(236, 240, 241));

        // Table
        tableModel = new TaskTableModel(tasks);
        JTable taskTable = new JTable(tableModel);
        taskTable.setRowHeight(35);
        taskTable.setFont(new Font("Arial", Font.PLAIN, 13));
        taskTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Center align all columns except Execute button
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < taskTable.getColumnCount() - 1; i++) {
            taskTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Progress column with custom renderer
        taskTable.getColumnModel().getColumn(8).setCellRenderer(new ProgressBarRenderer());

        // Execute button column
        taskTable.getColumnModel().getColumn(10).setCellRenderer(new ExecuteButtonRenderer());
        taskTable.getColumnModel().getColumn(10).setCellEditor(new ExecuteButtonEditor(taskTable));

        // Set column widths
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(40);   // ID
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(60);   // Product ID
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(60);   // Line ID
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(50);   // Qty
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(50);   // Completed
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(70);   // Client
        taskTable.getColumnModel().getColumn(6).setPreferredWidth(80);   // Start
        taskTable.getColumnModel().getColumn(7).setPreferredWidth(80);   // Finish
        taskTable.getColumnModel().getColumn(8).setPreferredWidth(80);   // Progress
        taskTable.getColumnModel().getColumn(9).setPreferredWidth(70);   // Status
        taskTable.getColumnModel().getColumn(10).setPreferredWidth(80);  // Execute

        JScrollPane scrollPane = new JScrollPane(taskTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(236, 240, 241));

        JLabel statusLabel = new JLabel("Total tasks: " + tasks.size());
        statusLabel.setForeground(Color.DARK_GRAY);
        panel.add(statusLabel);

        return panel;
    }

    // =========================================================================
    // INNER CLASS: Table Model for Tasks
    // =========================================================================
    private static class TaskTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Product", "Line", "Qty", "Done", "Client", "Start", "Finish", "Progress", "Status", "Execute"};
        private final List<Task> tasks;

        public TaskTableModel(List<Task> tasks) {
            this.tasks = new ArrayList<>(tasks);
        }

        public Task getTaskAt(int row) {
            return tasks.get(row);
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
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 10; // Only Execute column is editable
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
                case 10: return "Execute";
                default: return null;
            }
        }
    }

    // =========================================================================
    // INNER CLASS: Progress Bar Renderer
    // =========================================================================
    private static class ProgressBarRenderer extends JProgressBar implements javax.swing.table.TableCellRenderer {
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

            // Color based on progress
            if (progress < 30) {
                setForeground(new Color(231, 76, 60));  // Red
            } else if (progress < 70) {
                setForeground(new Color(241, 196, 15)); // Yellow
            } else {
                setForeground(new Color(39, 174, 96));  // Green
            }

            return this;
        }
    }

    // =========================================================================
    // INNER CLASS: Execute Button Renderer
    // =========================================================================
    private static class ExecuteButtonRenderer extends JButton implements TableCellRenderer {
        public ExecuteButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Execute");
            setBackground(new Color(46, 204, 113)); // Green
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 11));
            return this;
        }
    }

    // =========================================================================
    // INNER CLASS: Execute Button Editor
    // =========================================================================
    private class ExecuteButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private Task currentTask;

        public ExecuteButtonEditor(JTable table) {
            super(new JCheckBox());
            button = new JButton("Execute");
            button.setOpaque(true);
            button.setBackground(new Color(46, 204, 113));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 11));

            button.addActionListener(e -> {
                fireEditingStopped();
                if (currentTask != null) {
                    TaskInventory.executeTask(currentTask);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentTask = tableModel.getTaskAt(row);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Execute";
        }
    }
}
