package com.example.smartkartapp;

public class UserMessage {
    private String userId;
    private String message;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userRole;
    private long timestamp;

    // Constructor
    public UserMessage(String userId, String message, String userName, String userEmail,
                       String userPhone, String userRole, long timestamp) {
        this.userId = userId;
        this.message = message;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userRole = userRole;
        this.timestamp = timestamp;
    }

    // Getter and Setter methods
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
