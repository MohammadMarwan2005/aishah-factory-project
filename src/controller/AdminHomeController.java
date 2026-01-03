package controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminHomeController extends JFrame {

    public AdminHomeController() {
        setTitle("Production Admin Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(142, 68, 173));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Production Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(236, 240, 241));

        // Dashboard cards
        panel.add(createCard("Manage Items", "Manage inventory items", new Color(46, 204, 113), this::openItemController));
        panel.add(createCard("Manage Products", "Manage products and their tasks", new Color(155, 89, 182), this::openProductController));

        return panel;
    }

    private JPanel createCard(String title, String description, Color color, Runnable onClick) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(color);

        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel(new BorderLayout(5, 10));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.CENTER);

        card.add(textPanel, BorderLayout.CENTER);

        // Make card clickable if action is provided
        if (onClick != null) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    onClick.run();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(new Color(245, 245, 245));
                    textPanel.setBackground(new Color(245, 245, 245));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBackground(Color.WHITE);
                    textPanel.setBackground(Color.WHITE);
                }
            });
        }

        return card;
    }

    private void openItemController() {
        ItemController itemController = new ItemController();
        itemController.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        itemController.setVisible(true);
    }

    private void openTaskController() {
        TaskController taskController = new TaskController();
        taskController.setVisible(true);
    }

    private void openProductLineController() {
        ProductLineController productLineController = new ProductLineController();
        productLineController.setVisible(true);
    }

    private void openProductController() {
        ProductController productController = new ProductController();
        productController.setVisible(true);
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(236, 240, 241));

        JLabel statusLabel = new JLabel("Logged in as: Production Admin");
        statusLabel.setForeground(Color.DARK_GRAY);
        panel.add(statusLabel);

        return panel;
    }
}
