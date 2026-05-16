package com.company.system.model;

public class DepartmentStats {

    private final String departmentName;
    private final int employeeCount;

    public DepartmentStats(String departmentName, int employeeCount) {
        this.departmentName = departmentName;
        this.employeeCount = employeeCount;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }
}