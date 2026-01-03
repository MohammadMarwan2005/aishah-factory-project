package model.inventroy;

import model.io.FileManager;
import model.io.ObjectType;
import model.structure.Product;

import java.io.IOException;
import java.util.List;

public class ProductInventory {

    public static void addProduct(Product product) throws IOException {
        FileManager.saveObjectToFile(product, ObjectType.Product, product.getId());
    }

    public static void updateProduct(Product product) throws IOException {
        FileManager.saveObjectToFile(product, ObjectType.Product, product.getId());
    }

    public static void deleteProduct(int id) throws IOException {
        FileManager.deleteFile(ObjectType.Product, id);
    }

    public static Product getProduct(int id) throws IOException, ClassNotFoundException {
        return FileManager.getObjectFromFile(ObjectType.Product, id);
    }

    public static List<Product> getAllProducts() {
        return FileManager.getAllObjects(ObjectType.Product);
    }
}
