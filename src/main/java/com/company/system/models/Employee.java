package com.company.system.models;

import java.sql.Date;

public class Employee {

    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String position;
    private final int departmentId;
    private final Date hireDate;
    private final double baseSalary;
    private final String status;

    public Employee(int id, String firstName, String lastName, String email,
                    String phone, String position, int departmentId,
                    Date hireDate, double baseSalary, String status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.departmentId = departmentId;
        this.hireDate = hireDate;
        this.baseSalary = baseSalary;
        this.status = status;
    }

    public int getId() { return id; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getEmail() { return email; }

    public String getPhone() { return phone; }

    public String getPosition() { return position; }

    public int getDepartmentId() { return departmentId; }

    public Date getHireDate() { return hireDate; }

    public double getBaseSalary() { return baseSalary; }

    public String getStatus() { return status; }
}