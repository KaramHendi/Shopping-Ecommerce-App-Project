package com.example.smartkartapp;

public class MemberReg {

    String id, username, password, phone, role;

    // Default constructor
    public MemberReg() {
    }

    // Parameterized constructor
    public MemberReg(String id, String username, String password, String phone, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.role = role;  // Set the role field
    }

    // Getters and Setters for all fields
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
