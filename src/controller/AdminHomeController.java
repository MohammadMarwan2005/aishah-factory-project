package controller;

import javax.swing.*;
import java.awt.*;

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
        panel.add(createCard("Production Lines", "Manage production lines and tasks", new Color(231, 76, 60)));
        panel.add(createCard("Item Management", "Manage inventory items", new Color(46, 204, 113)));
        panel.add(createCard("Task Assignment", "Assign and track production tasks", new Color(52, 152, 219)));
        panel.add(createCard("Quality Control", "Review and approve products", new Color(241, 196, 15)));

        return panel;
    }

    private JPanel createCard(String title, String description, Color color) {
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

        return card;
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
