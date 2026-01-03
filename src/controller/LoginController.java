package controller;

import model.inventroy.LoginInventory;
import model.io.ErrorLogger;
import model.structure.Role;

import javax.swing.*;
import java.awt.*;

public class LoginController extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginController() {
        setTitle("Factory System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout(10, 10));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createLoginPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titleLabel = new JLabel("Factory Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameLabel, gbc);

        // Username field
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameField, gbc);

        // Password label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(passwordLabel, gbc);

        // Password field
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(20, 8, 8, 8);
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(39, 174, 96));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> onLogin());
        panel.add(loginButton, gbc);

        // Enter key triggers login
        passwordField.addActionListener(e -> onLogin());

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(236, 240, 241));

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.DARK_GRAY);
        panel.add(statusLabel);

        return panel;
    }

    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password");
            statusLabel.setForeground(new Color(231, 76, 60));
            return;
        }

        Role role = LoginInventory.login(username, password);

        if (role == null) {
            statusLabel.setText("Invalid username or password");
            statusLabel.setForeground(new Color(231, 76, 60));
            ErrorLogger.logError("Failed login attempt for username: " + username);
            return;
        }

        // Success - navigate to appropriate home screen
        statusLabel.setText("Login successful!");
        statusLabel.setForeground(new Color(39, 174, 96));

        // Open the appropriate home screen based on role
        JFrame homeScreen = role.createHomeScreen();
        homeScreen.setVisible(true);

        // Close login window
        this.dispose();
    }
}
