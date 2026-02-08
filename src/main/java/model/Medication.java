package model;

public class Medication {

    private String medicineName;
    private int currentStock;
    private int lowStockLevel;
    private int replenishAmount; // if 0, no replenish request.

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }

    public int getLowStockLevel() {
        return lowStockLevel;
    }

    public void setLowStockLevel(int lowStockLevel) { this.lowStockLevel = lowStockLevel; }

    public int getReplenishAmount() {
        return replenishAmount;
    }

    public void setReplenishAmount(int replenishAmount) {
        this.replenishAmount = replenishAmount;
    }

    public boolean isLowStock() {
        return currentStock <= lowStockLevel;
    }
}
