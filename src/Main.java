import controller.LoginController;
import model.inventroy.LoginInventory;

import javax.swing.*;

/**
 * Main Application Entry Point.
 * Launches the Swing application.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize default users if they don't exist
        LoginInventory.initializeUsersIfNeeded();

        // Run on the Event Dispatch Thread (Swing best practice)
        SwingUtilities.invokeLater(() -> {
            LoginController loginController = new LoginController();
            loginController.setVisible(true);
        });
    }
}
