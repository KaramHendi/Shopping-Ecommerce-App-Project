package com.example.smartkartapp;

public class StaffReg {
    String name, password, id, phone, role;

    public StaffReg() {
        // Default constructor required for Firebase
    }

    public StaffReg(String name, String password, String id, String phone, String role) {
        this.name = name;
        this.password = password;
        this.id = id;
        this.phone = phone;
        this.role = role;  // Set the role as 'staff'
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;  // Get the role
    }
}
