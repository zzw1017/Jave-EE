package com.example.attendance.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String realName;
    private String role;

    public RegisterRequest() {
    }

    public RegisterRequest(String password, String username, String realName, String role) {
        this.password = password;
        this.username = username;
        this.realName = realName;
        this.role = role;
    }

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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}