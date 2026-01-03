package model.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {


    public static void saveObjectToFile(Serializable object, ObjectType objectType, int id) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(objectType.getPath(id)))) {
            oos.writeObject(object);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObjectFromFile(ObjectType objectType, int id) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objectType.getPath(id)))) {
            return (T) ois.readObject();
        }
    }

    public static boolean deleteFile(ObjectType objectType, int id) {
        return new File(objectType.getPath(id)).delete();
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getAllObjects(ObjectType objectType) {
        List<T> objects = new ArrayList<>();
        String prefix = objectType.getPrefix(); // ex: Item
        
        File folder = new File(".");
        File[] allFiles = folder.listFiles();
        
        if (allFiles == null) {
            return objects;
        }
        
        for (File file : allFiles) {
            String name = file.getName();

            // Use prefix + "_" to avoid matching similar prefixes (e.g., Product vs ProductLine)
            if (name.startsWith(prefix + "_") && name.endsWith(".dat")) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                    T obj = (T) ois.readObject();
                    objects.add(obj);
                    ois.close();
                } catch (IOException | ClassNotFoundException e) {
                    ErrorLogger.logError(e);
                }
            }
        }
        
        return objects;
    }

    /**
     * Generates the next unique ID for a given object type.
     * Simply finds the highest existing ID and returns (max + 1).
     */
    public static int generateNextId(ObjectType objectType) {
        String prefix = objectType.getPrefix(); // ex: Item
        int maxId = 0;

        File folder = new File(".");
        File[] allFiles = folder.listFiles();

        if (allFiles == null) {
            return 1; // Start from 1 if no files exist
        }

        for (File file : allFiles) {
            String name = file.getName(); // ex: Item_5.dat

            // Use prefix + "_" to avoid matching similar prefixes (e.g., Product vs ProductLine)
            if (name.startsWith(prefix + "_") && name.endsWith(".dat")) {
                // Extract the ID from filename: Item_5.dat -> 5
                String idPart = name.substring(prefix.length() + 1, name.length() - 4);
                try {
                    int id = Integer.parseInt(idPart);
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    ErrorLogger.logError(e);
                }
            }
        }

        return maxId + 1;
    }
}

