package com.company.system.model;

import java.sql.Timestamp;

public class User {

    private final int id;
    private final String username;
    private final Integer employeeId;
    private final String employeeName;
    private final String passwordHash;
    private final String role;
    private final Timestamp createdAt;
    private final boolean mustChangePassword;

    public User(int id, String username,String passwordHash, String role, Timestamp createdAt) {
        this(id, username, null, null, passwordHash, role, createdAt, false);
    }

    public User(int id, String username, Integer employeeId, String employeeName, String passwordHash, String role, Timestamp createdAt) {
        this(id, username, employeeId, employeeName, passwordHash, role, createdAt, false);
    }

    public User(int id, String username, Integer employeeId, String employeeName, String passwordHash, String role, Timestamp createdAt, boolean mustChangePassword) {
        this.id = id;
        this.username = username;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.passwordHash = passwordHash;
        this.role=role;
        this.createdAt = createdAt;
        this.mustChangePassword = mustChangePassword;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getPasswordHash() {return passwordHash;}

    public String getRole() {return role;}

    public Timestamp getCreatedAt() {return createdAt;}

    public boolean mustChangePassword() {return mustChangePassword;}
}
