package com.company.system.models;

public class SalaryByDepartmentStats {

    private final String departmentName;
    private final double averageSalary;

    public SalaryByDepartmentStats(String departmentName, double averageSalary) {
        this.departmentName = departmentName;
        this.averageSalary = averageSalary;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public double getAverageSalary() {
        return averageSalary;
    }
}
