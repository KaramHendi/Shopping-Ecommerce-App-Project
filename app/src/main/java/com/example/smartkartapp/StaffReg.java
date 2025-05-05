package com.example.smartkartapp;

public class StaffReg {
    String staffname, password, id, phone, role;

    public StaffReg() {
        // Default constructor required for Firebase
    }

    public StaffReg(String staffname, String password, String id, String phone, String role) {
        this.staffname = staffname;
        this.password = password;
        this.id = id;
        this.phone = phone;
        this.role = role;  // Set the role as 'staff'
    }

    public String getStaffname() {
        return staffname;
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