package model.structure;

import model.io.FileManager;
import model.io.ObjectType;

import java.util.Map;

public class Product implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String name;
    private final Map<Integer, Integer> requiredItems;

    // Constructor with auto-generated ID
    public Product(String name, Map<Integer, Integer> requiredItems) {
        this.id = FileManager.generateNextId(ObjectType.Product);
        this.name = name;
        this.requiredItems = requiredItems;
    }

    // Constructor with manual ID (for loading from file)
    public Product(int id, String name, Map<Integer, Integer> requiredItems) {
        this.id = id;
        this.name = name;
        this.requiredItems = requiredItems;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Product{id=").append(id)
          .append(", name='").append(name).append('\'')
          .append(", requiredItems={");

        boolean first = true;
        for (Map.Entry<Integer, Integer> entry : requiredItems.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.getKey())
              .append(": ").append(entry.getValue());
            first = false;
        }
        sb.append("}}");
        return sb.toString();
    }

}
