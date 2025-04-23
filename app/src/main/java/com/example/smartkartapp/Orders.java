package com.example.smartkartapp;

public class Orders {
    private String id;
    private String spec;
    private String custname;
    private String custphone;
    private String custaddr;
    private String custpass;
    private int price;

    public Orders() {
        // Needed for Firebase
    }

    public Orders(String id, String spec, String custname, String custphone, String custaddr, String custpass, int price) {
        this.id = id;
        this.spec = spec;
        this.custname = custname;
        this.custphone = custphone;
        this.custaddr = custaddr;
        this.custpass = custpass;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getSpec() {
        return spec;
    }

    public String getCustname() {
        return custname;
    }

    public String getCustphone() {
        return custphone;
    }

    public String getCustaddr() {
        return custaddr;
    }

    public String getCustpass() {
        return custpass;
    }

    public int getPrice() {
        return price;
    }
}