package model.structure;

import model.io.FileManager;
import model.io.ObjectType;

import java.io.Serializable;
import java.util.List;

public class ProductLine implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String name;
    private final ProductLineStatus status;
    private final List<Task> tasks;

    // Constructor with all fields
    public ProductLine(int id, String name, ProductLineStatus status, List<Task> tasks) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.tasks = tasks;
    }

    // Constructor omitting id (for auto-generating or setting later)
    public ProductLine(String name, ProductLineStatus status, List<Task> tasks) {
        this.id = FileManager.generateNextId(ObjectType.ProductLine);
        this.name = name;
        this.status = status;
        this.tasks = tasks;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProductLineStatus getStatus() {
        return status;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public String toString() {
        return "ProductLine{id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", tasks=" + tasks +
                '}';
    }
}
