package com.company.system.model;

public class DashboardStats {

    private final int totalEmployees;
    private final int totalContracts;
    private final int totalSalaries;
    private final int totalDepartments;
    private final int activeContracts;
    private final int pendingContracts;
    private final int expiredContracts;
    private final int expiringContracts;
    private final double averageSalary;

    public DashboardStats(
            int totalEmployees,
            int totalContracts,
            int totalSalaries,
            int totalDepartments,
            int activeContracts,
            int pendingContracts,
            int expiredContracts,
            int expiringContracts,
            double averageSalary
    ) {
        this.totalEmployees = totalEmployees;
        this.totalContracts = totalContracts;
        this.totalSalaries = totalSalaries;
        this.totalDepartments = totalDepartments;
        this.activeContracts = activeContracts;
        this.pendingContracts = pendingContracts;
        this.expiredContracts = expiredContracts;
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

    public int getTotalDepartments() {
        return totalDepartments;
    }

    public int getActiveContracts() {
        return activeContracts;
    }

    public int getPendingContracts() {
        return pendingContracts;
    }

    public int getExpiredContracts() {
        return expiredContracts;
    }

    public int getExpiringContracts() {
        return expiringContracts;
    }

    public double getAverageSalary() {
        return averageSalary;
    }
}
