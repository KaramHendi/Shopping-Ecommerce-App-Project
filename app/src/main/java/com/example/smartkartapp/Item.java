package com.example.smartkartapp;

public class Item {
    private String itemId;
    private int quantity;

    public Item() {}

    public Item(String itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public String getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }
}
