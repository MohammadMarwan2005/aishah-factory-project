import controller.ItemController;

import javax.swing.*;

/**
 * Main Application Entry Point.
 * Launches the Swing application.
 */
public class Main {

    public static void main(String[] args) {
        // Run on the Event Dispatch Thread (Swing best practice)
        SwingUtilities.invokeLater(() -> {
            ItemController controller = new ItemController();
            controller.setVisible(true);
        });
    }
}
