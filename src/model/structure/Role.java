package model.structure;

import controller.AdminHomeController;
import controller.ManagerHomeController;

import javax.swing.*;

public enum Role {
    Manager,
    ProductionAdmin;

    public JFrame createHomeScreen() {
        return switch (this) {
            case Manager -> new ManagerHomeController();
            case ProductionAdmin -> new AdminHomeController();
        };
    }
}
