package com.company.system.model;

public class DashboardStats {

    private final int totalEmployees;
    private final int totalContracts;
    private final int totalSalaries;
    private final int activeContracts;
    private final int expiringContracts;
    private final double averageSalary;

    public DashboardStats(
            int totalEmployees,
            int totalContracts,
            int totalSalaries,
            int activeContracts,
            int expiringContracts,
            double averageSalary
    ) {
        this.totalEmployees = totalEmployees;
        this.totalContracts = totalContracts;
        this.totalSalaries = totalSalaries;
        this.activeContracts = activeContracts;
        this.expiringContracts = expiringContracts;
        this.averageSalary = averageSalary;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public int getTotalContracts() {
        return totalContracts;
    }

    public int getTotalSalaries() {
        return totalSalaries;
    }

    public int getActiveContracts() {
        return activeContracts;
    }

    public int getExpiringContracts() {
        return expiringContracts;
    }

    public double getAverageSalary() {
        return averageSalary;
    }
}