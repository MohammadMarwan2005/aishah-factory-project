package model.inventroy;

import model.io.ErrorLogger;
import model.io.FileManager;
import model.io.ObjectType;
import model.structure.Role;
import model.structure.User;

import java.io.IOException;
import java.util.List;

import static model.structure.User.INITIAL_USERS;

public class LoginInventory {

    public static Role login(String username, String password) {
        List<User> users = getAllSavedUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user.getRole();
            }
        }
        return null;
    }

    private static List<User> getAllSavedUsers() {
        return FileManager.getAllObjects(ObjectType.User);
    }

    public static void initializeUsersIfNeeded() {
        for (User user : INITIAL_USERS) {
            if (login(user.getUsername(), user.getPassword()) == null) {
                try {
                    User toSaveUser = new User(user.getUsername(), user.getPassword(), user.getRole());
                    FileManager.saveObjectToFile(toSaveUser, ObjectType.User, toSaveUser.getId());
                } catch (IOException e) {
                    ErrorLogger.logError(e);
                }
            }
        }
        // already exist, continue
    }
}
