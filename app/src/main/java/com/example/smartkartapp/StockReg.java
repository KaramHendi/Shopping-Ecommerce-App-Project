package com.example.smartkartapp;

public class StockReg {
    private String id; // Firebase key (e.g., "item1")
    private int item_id; // The unique item number
    private String itemName;
    private int currentStockAvailaible;

    // Default constructor required for Firebase
    public StockReg() {
    }

    // Parameterized constructor
    public StockReg(String id, int item_id, String itemName, int currentStockAvailaible) {
        this.id = id;
        this.item_id = item_id;
        this.itemName = itemName;
        this.currentStockAvailaible = currentStockAvailaible;
    }

    // Getter and setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getCurrentStockAvailaible() {
        return currentStockAvailaible;
    }

    public void setCurrentStockAvailaible(int currentStockAvailaible) {
        this.currentStockAvailaible = currentStockAvailaible;
    }
}
