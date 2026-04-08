package com.example.attendance.entity;

import java.sql.Timestamp;

public class User {
    private String id;
    private String username;
    private String password;
    private String realName;
    private String role;
    private Timestamp createTime;

    public User() {
    }

    public User(String username, String id, String realName, String password, Timestamp createTime, String role) {
        this.username = username;
        this.id = id;
        this.realName = realName;
        this.password = password;
        this.createTime = createTime;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
