package com.company.system.model;

import java.sql.Timestamp;

public class User {

    private final int id;
    private final String username;
    private final String passwordHash;
    private final String role;
    private final Timestamp createdAt;

    public User(int id, String username,String passwordHash, String role, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role=role;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {return passwordHash;}

    public String getRole() {return role;}

    public Timestamp getCreatedAt() {return createdAt;}
}