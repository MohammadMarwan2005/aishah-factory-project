package model.structure;

import model.io.FileManager;
import model.io.ObjectType;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String username;
    private final String password;
    private final Role role;

    public User(String username, String password, Role role) {
        this.id = FileManager.generateNextId(ObjectType.User);
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(int id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // IDs are placeholders, real IDs assigned when saved
    public static final List<User> INITIAL_USERS = List.of(
            new User(0, "manager", "manager", Role.Manager),
            new User(0, "admin", "admin", Role.ProductionAdmin)
    );


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
