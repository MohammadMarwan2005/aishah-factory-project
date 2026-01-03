package model.inventroy;

import model.io.FileManager;
import model.io.ObjectType;
import model.structure.Task;
import model.thread.TaskExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskInventory {

    public static void addTask(Task task) throws IOException {
        FileManager.saveObjectToFile(task, ObjectType.Task, task.getId());
    }

    public static void updateTask(Task task) throws IOException {
        FileManager.saveObjectToFile(task, ObjectType.Task, task.getId());
    }

    public static void deleteTask(int id) throws IOException {
        FileManager.deleteFile(ObjectType.Task, id);
    }

    public static Task getTask(int id) throws IOException, ClassNotFoundException {
        return FileManager.getObjectFromFile(ObjectType.Task, id);
    }

    public static List<Task> getAllTasks() {
        return FileManager.getAllObjects(ObjectType.Task);
    }

    public static List<Task> getProductTasks(int productId) {
        List<Task> allTasks = getAllTasks();
        List<Task> result = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.getProductId() != null && task.getProductId() == productId) {
                result.add(task);
            }
        }
        return result;
    }

    public static List<Task> getProductLineTasks(int productLineId) {
        List<Task> allTasks = getAllTasks();
        List<Task> result = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.getProductLineId() != null && task.getProductLineId() == productLineId) {
                result.add(task);
            }
        }
        return result;
    }

    /**
     * Executes a task on an independent thread.
     * Checks item availability, calculates total time, and shows alert when finished.
     */
    public static void executeTask(Task task) {
        TaskExecutor executor = new TaskExecutor(task);
        Thread thread = new Thread(executor);
        thread.start();
    }
}
