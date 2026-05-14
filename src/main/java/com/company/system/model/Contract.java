package com.company.system.model;

import java.sql.Date;

public class Contract {

    private final int id;
    private final int employeeId;
    private final String contractType;
    private final Date startDate;
    private final Date endDate;
    private final double salary;
    private final String status;

    public Contract(
            int id,
            int employeeId,
            String contractType,
            Date startDate,
            Date endDate,
            double salary,
            String status
    ) {

        this.id = id;
        this.employeeId = employeeId;
        this.contractType = contractType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
        this.status = status;
    }

    public int getId() { return id; }

    public int getEmployeeId() { return employeeId; }

    public String getContractType() { return contractType; }

    public Date getStartDate() { return startDate; }

    public Date getEndDate() { return endDate; }

    public double getSalary() { return salary; }

    public String getStatus() { return status; }
}