package com.example.smartkartapp;

public class GetUserLogin {
    String uname, uphone, id;

    public GetUserLogin() {
        // Default constructor required for Firebase
    }

    public GetUserLogin(String uname, String uphone, String id) {
        this.uname = uname;
        this.uphone = uphone;
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public String getUphone() {
        return uphone;
    }

    public String getId() {
        return id;
    }
}
