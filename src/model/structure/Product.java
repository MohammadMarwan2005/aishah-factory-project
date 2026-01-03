package model.structure;

import model.io.FileManager;
import model.io.ObjectType;

import java.util.Map;

public class Product implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String name;
    private final Map<Integer, Integer> requiredItems;
    private final int estimatedProductionTimeSeconds;

    // Constructor with auto-generated ID
    public Product(String name, Map<Integer, Integer> requiredItems, int estimatedProductionTimeSeconds) {
        this.estimatedProductionTimeSeconds = estimatedProductionTimeSeconds;
        this.id = FileManager.generateNextId(ObjectType.Product);
        this.name = name;
        this.requiredItems = requiredItems;
    }

    // Constructor with manual ID (for loading from file)
    public Product(int id, String name, Map<Integer, Integer> requiredItems, int estimatedProductionTimeSeconds) {
        this.id = id;
        this.name = name;
        this.requiredItems = requiredItems;
        this.estimatedProductionTimeSeconds = estimatedProductionTimeSeconds;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<Integer, Integer> getRequiredItems() {
        return requiredItems;
    }

    public int getEstimatedProductionTimeSeconds() {
        return estimatedProductionTimeSeconds;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Product: ");
        result.append("Use ID: ").append(id).append(". ");
        result.append("Name: '").append(name).append("'. ");
        result.append("Gather Required Items: {");
        boolean first = true;
        for (Map.Entry<Integer, Integer> entry : requiredItems.entrySet()) {
            if (!first) {
                result.append(", ");
            }
            result.append("Item ").append(entry.getKey());
            result.append(": Obtain ").append(entry.getValue());
            first = false;
        }
        result.append("}.");
        return result.toString();
    }

}
