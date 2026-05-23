package com.company.system.model;

import java.sql.Date;

public class Salary {

    private final int id;
    private final int employeeId;
    private final double grossSalary;
    private final double bonus;
    private final double deductions;

    // fields te reja
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
            double amount,
            double bonus,
            double deductions,
            Date paymentDate
    ) {

        this.id = id;
        this.employeeId = employeeId;
        this.amount = amount;
        this.bonus = bonus;
        this.deductions = deductions;
        this.paymentDate = paymentDate;
    }

    public int getId() { return id; }

    public int getEmployeeId() { return employeeId; }

    public double getAmount() { return amount; }

    public double getBonus() { return bonus; }

    public double getDeductions() { return deductions; }

    public Date getPaymentDate() { return paymentDate; }
}