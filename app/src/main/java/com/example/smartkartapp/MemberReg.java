package com.example.smartkartapp;

public class MemberReg {
    private String phone;
    private String name;
    private String password;
    private String role;

    public MemberReg() {
        // Needed for Firebase
    }

    public MemberReg(String phone, String name, String password, String role) {
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
