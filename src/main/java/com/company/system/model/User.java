package com.company.system.model;

import java.sql.Timestamp;

public class User {

    private final int id;
    private final String username;
    private final String password;
    private final Timestamp createdAt;

    public User(int id, String username, String password, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Timestamp getCreatedAt() {return createdAt;}
}