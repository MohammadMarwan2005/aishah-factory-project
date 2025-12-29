package model.inventroy;

import model.io.FileManager;
import model.io.ObjectType;
import model.structure.Item;

import java.io.IOException;
import java.util.List;

public class ItemInventory {

    public static void addItem(Item item) throws IOException {
        FileManager.saveObjectToFile(item, ObjectType.Item, item.getId());
    }

    public static void deleteItem(int id) throws IOException {
        FileManager.deleteFile(ObjectType.Item, id);
    }

    public static Item getItem(int id) throws IOException, ClassNotFoundException {
        return FileManager.getObjectFromFile(ObjectType.Item, id);
    }

    public static List<Item> getAllItems() {
        return FileManager.getAllObjects(ObjectType.Item);
    }
}
