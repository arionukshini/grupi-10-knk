package com.company.system.model;

import java.sql.Date;

public class Salary {

    private final int id;
    private final int employeeId;
    private final double grossSalary;
    private final double bonus;
    private final double deductions;
    private final int vacationDays;
    private final double workHours;
    private final double overtimeHours;
    private final double dailyRate;
    private final double overtimePay;
    private final double netSalary;

    private final Date paymentDate;


    public Salary(
            int id,
            int employeeId,
            double grossSalary,
            double bonus,
            double deductions,
            int vacationDays,
            double workHours,
            double overtimeHours,
            double dailyRate,
            double overtimePay,
            double netSalary,
            Date paymentDate
    )
    {
        this.id = id;
        this.employeeId = employeeId;
        this.grossSalary = grossSalary;
        this.bonus = bonus;
        this.deductions = deductions;
        this.vacationDays = vacationDays;
        this.workHours = workHours;
        this.overtimeHours = overtimeHours;
        this.dailyRate = dailyRate;
        this.overtimePay = overtimePay;
        this.netSalary = netSalary;
        this.paymentDate = paymentDate;
    }

    public int getId() { return id; }

    public int getEmployeeId() { return employeeId; }

    public double getAmount() { return amount; }

    public double getBonus() { return bonus; }

    public double getDeductions() { return deductions; }

    public Date getPaymentDate() { return paymentDate; }
}