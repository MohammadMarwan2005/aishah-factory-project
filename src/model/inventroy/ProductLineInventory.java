package model.inventroy;

import model.io.FileManager;
import model.io.ObjectType;
import model.structure.ProductLine;

import java.io.IOException;
import java.util.List;

public class ProductLineInventory {

    public static void addProductLine(ProductLine productLine) throws IOException {
        FileManager.saveObjectToFile(productLine, ObjectType.ProductLine, productLine.getId());
    }

    public static void updateProductLine(ProductLine productLine) throws IOException {
        FileManager.saveObjectToFile(productLine, ObjectType.ProductLine, productLine.getId());
    }

    public static void deleteProductLine(int id) throws IOException {
        FileManager.deleteFile(ObjectType.ProductLine, id);
    }

    public static ProductLine getProductLine(int id) throws IOException, ClassNotFoundException {
        return FileManager.getObjectFromFile(ObjectType.ProductLine, id);
    }

    public static List<ProductLine> getAllProductLines() {
        return FileManager.getAllObjects(ObjectType.ProductLine);
    }
}
