package model.thread;

import model.inventroy.ItemInventory;
import model.inventroy.ProductInventory;
import model.inventroy.TaskInventory;
import model.structure.Item;
import model.structure.Product;
import model.structure.Task;
import model.structure.TaskStatus;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskExecutor implements Runnable {
    private final Task task;

    public TaskExecutor(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        // Step 1: Check if task has a product
        if (task.getProductId() == null) {
            showAlert("Error", "Task has no product assigned. Cannot execute.");
            return;
        }

        // Step 2: Get the product
        Product product;
        try {
            product = ProductInventory.getProduct(task.getProductId());
        } catch (IOException | ClassNotFoundException e) {
            showAlert("Error", "Failed to load product: " + e.getMessage());
            return;
        }

        // Step 3: Calculate total items needed
        int taskQuantity = task.getQuantity() - task.getCompletedQuantity(); // Remaining to produce
        if (taskQuantity <= 0) {
            showAlert("Info", "Task is already completed!");
            return;
        }

        Map<Integer, Integer> requiredItems = product.getRequiredItems();
        Map<Integer, Integer> totalItemsNeeded = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : requiredItems.entrySet()) {
            int itemId = entry.getKey();
            int quantityPerProduct = entry.getValue();
            int totalNeeded = quantityPerProduct * taskQuantity;
            totalItemsNeeded.put(itemId, totalNeeded);
        }

        // Step 4: Check item availability
        StringBuilder itemReport = new StringBuilder();
        itemReport.append("Items Required for Task #").append(task.getId()).append(":\n\n");

        List<Item> allItems = ItemInventory.getAllItems();
        Map<Integer, Item> itemMap = new HashMap<>();
        for (Item item : allItems) {
            itemMap.put(item.getId(), item);
        }

        boolean hasEnoughItems = true;
        for (Map.Entry<Integer, Integer> entry : totalItemsNeeded.entrySet()) {
            int itemId = entry.getKey();
            int needed = entry.getValue();
            Item item = itemMap.get(itemId);

            if (item == null) {
                itemReport.append("  - Item #").append(itemId).append(": NOT FOUND\n");
                hasEnoughItems = false;
            } else {
                int available = item.getQuantity();
                String status = available >= needed ? "OK" : "INSUFFICIENT";
                itemReport.append("  - ").append(item.getName())
                        .append(": Need ").append(needed)
                        .append(", Have ").append(available)
                        .append(" [").append(status).append("]\n");
                if (available < needed) {
                    hasEnoughItems = false;
                }
            }
        }

        if (!hasEnoughItems) {
            showAlert("Insufficient Items", itemReport.toString() + "\nCannot execute task - not enough items!");
            return;
        }

        // Step 5: Calculate total estimated time
        int timePerProduct = product.getEstimatedProductionTimeSeconds();
        int totalTimeSeconds = timePerProduct * taskQuantity;

        // Show start message
        showAlert("Task Started",
                itemReport.toString() +
                        "\nEstimated Time: " + formatTime(totalTimeSeconds) +
                        "\n\nTask execution started...");

        // Step 6: Simulate execution (sleep for the estimated time)
        try {
            Thread.sleep(totalTimeSeconds * 1000L);
        } catch (InterruptedException e) {
            showAlert("Error", "Task was interrupted: " + e.getMessage());
            return;
        }

        // Step 7: Update task status to Completed
        Task completedTask = new Task(
                task.getId(),
                task.getProductId(),
                task.getProductLineId(),
                task.getQuantity(),
                task.getQuantity(), // completedQuantity = quantity (fully completed)
                task.getClientName(),
                task.getStartDate(),
                task.getFinishDate(),
                TaskStatus.Completed
        );

        try {
            TaskInventory.updateTask(completedTask);
        } catch (IOException e) {
            showAlert("Error", "Failed to update task status: " + e.getMessage());
            return;
        }

        // Step 8: Show completion alert
        showAlert("Task Completed",
                "Task #" + task.getId() + " for product '" + product.getName() + "' has been completed!\n\n" +
                        "Produced: " + taskQuantity + " units\n" +
                        "Time taken: " + formatTime(totalTimeSeconds) + "\n\n" +
                        "Status updated to: Completed");
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d hr %d min %d sec", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d min %d sec", minutes, seconds);
        } else {
            return String.format("%d sec", seconds);
        }
    }

    private void showAlert(String title, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
