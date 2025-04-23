package com.example.smartkartapp;

public class Orders {
    private String id;
    private String spec;
    private String custname;
    private String custphone;
    private String custaddr;
    private String custpass;
    private int price;
    private String status; // "pending" or "completed"

    public Orders() {}

    public Orders(String id, String spec, String custname, String custphone, String custaddr, String custpass, int price, String status) {
        this.id = id;
        this.spec = spec;
        this.custname = custname;
        this.custphone = custphone;
        this.custaddr = custaddr;
        this.custpass = custpass;
        this.price = price;
        this.status = status;
    }

    public String getId() { return id; }
    public String getSpec() { return spec; }
    public String getCustname() { return custname; }
    public String getCustphone() { return custphone; }
    public String getCustaddr() { return custaddr; }
    public String getCustpass() { return custpass; }
    public int getPrice() { return price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
