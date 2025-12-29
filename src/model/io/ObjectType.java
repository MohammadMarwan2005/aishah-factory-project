package model.io;

public enum ObjectType {
    Item, Product, ProductLine, Task;

    public String getPath(int id) {
        return this.toString() + "_" + id + ".dat"; // Item_1.dat
    }

    public String getPrefix() {
        return toString(); // Item
        //
    }
}
