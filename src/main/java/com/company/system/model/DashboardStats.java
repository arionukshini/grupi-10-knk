package com.company.system.model;

public class DashboardStats {

    private final int totalEmployees;
    private final int totalDepartments;
    private final int activeContracts;
    private final double averageSalary;

    public DashboardStats(
            int totalEmployees,
            int totalDepartments,
            int activeContracts,
            double averageSalary
    ) {
        this.totalEmployees = totalEmployees;
        this.totalDepartments = totalDepartments;
        this.activeContracts = activeContracts;
        this.averageSalary = averageSalary;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public int getTotalDepartments() {
        return totalDepartments;
    }

    public int getActiveContracts() {
        return activeContracts;
    }

    public double getAverageSalary() {
        return averageSalary;
    }
}