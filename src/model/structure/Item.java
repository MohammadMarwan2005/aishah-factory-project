package model.structure;

import model.io.FileManager;
import model.io.ObjectType;

public class Item implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final int id ;
    private final String name;
    private final Category category;
    private final double price;
    private final int quantity ;
    private final int minQuantity ;

    // Constructor with auto-generated ID
    public Item(String name, Category category, double price, int quantity, int minQuantity) {
        this.id = FileManager.generateNextId(ObjectType.Item);
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
    }

    // Constructor with manual ID (for loading from file)
    public Item(int id, String name, Category category, double price, int quantity, int minQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    @Override
    public String toString() {
        return "Item{id=" + id +
               ", name='" + name + '\'' +
               ", category=" + category +
               ", price=" + price +
               ", quantity=" + quantity +
               ", minQuantity=" + minQuantity +
               '}';
    }

}
