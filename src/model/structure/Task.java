package model.structure;

import model.io.FileManager;
import model.io.ObjectType;

import java.io.Serializable;
import java.time.LocalDate;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final Integer productId;
    private final Integer productLineId;
    private final int quantity;
    private final int completedQuantity;
    private final String clientName;
    private final LocalDate startDate;
    private final LocalDate finishDate;
    private final TaskStatus status;

    // Constructor with explicit id
    public Task(int id, Integer productId, Integer productLineId, int quantity, int completedQuantity,
                String clientName, java.time.LocalDate startDate, java.time.LocalDate finishDate, TaskStatus status) {
        this.id = id;
        this.productId = productId;
        this.productLineId = productLineId;
        this.quantity = quantity;
        this.completedQuantity = completedQuantity;
        this.clientName = clientName;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.status = status;
    }

    // Constructor with auto-generated id
    public Task(Integer productId, Integer productLineId, int quantity, int completedQuantity,
                String clientName, java.time.LocalDate startDate, java.time.LocalDate finishDate, TaskStatus status) {
        this.id = FileManager.generateNextId(ObjectType.Task);
        this.productId = productId;
        this.productLineId = productLineId;
        this.quantity = quantity;
        this.completedQuantity = completedQuantity;
        this.clientName = clientName;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Integer getProductId() {
        return productId;
    }

    public Integer getProductLineId() {
        return productLineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getCompletedQuantity() {
        return completedQuantity;
    }

    public String getClientName() {
        return clientName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public TaskStatus getStatus() {
        return status;
    }
    
    public double getProgress() {
        return (double) completedQuantity / quantity * 100;
    }

    @Override
    public String toString() {
        return "Task{" +
               "id=" + id +
               ", productId=" + productId +
               ", productLineId=" + productLineId +
               ", quantity=" + quantity +
               ", completedQuantity=" + completedQuantity +
               ", clientName=" + clientName +
               ", startDate=" + startDate +
               ", finishDate=" + finishDate +
               ", status=" + status +
               '}';
    }
}
