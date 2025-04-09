package com.example.smartkartapp;

public class DeliverOrder {
    private String name, phone, id, address, orderDetails, deliveryStaffName, price, otp;

    // Default constructor (needed for Firebase)
    public DeliverOrder() {}

    // Constructor with all fields
    public DeliverOrder(String name, String phone, String id, String address, String orderDetails, String deliveryStaffName, String price, String otp) {
        this.name = name;
        this.phone = phone;
        this.id = id;
        this.address = address;
        this.orderDetails = orderDetails;
        this.deliveryStaffName = deliveryStaffName;
        this.price = price;
        this.otp = otp;
    }

    // Getters and setters for each field
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public String getDeliveryStaffName() {
        return deliveryStaffName;
    }

    public String getPrice() {
        return price;
    }

    public String getOtp() {
        return otp;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void setDeliveryStaffName(String deliveryStaffName) {
        this.deliveryStaffName = deliveryStaffName;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
