package model.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogger {

    private static final String ERROR_FILE = "errors.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logError(String errorMessage) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ERROR_FILE, true))) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            writer.println("[" + timestamp + "] " + errorMessage);
        } catch (IOException e) {
            System.err.println("Failed to write to error log: " + e.getMessage());
        }
    }

    public static void logError(Exception exception) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ERROR_FILE, true))) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            writer.println("[" + timestamp + "] " + exception.getClass().getName() + ": " + exception.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to write to error log: " + e.getMessage());
        }
    }
}

