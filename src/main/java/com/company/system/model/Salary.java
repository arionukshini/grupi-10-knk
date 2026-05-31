package com.company.system.model;

import java.sql.Date;

public class Salary {

    private final int id;
    private final int employeeId;
    private final String employeeName;

    private final double grossSalary;
    private final double bonus;
    private final double deductions;

    private final int workedDays;
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
            String employeeName,
            double grossSalary,
            double bonus,
            double deductions,
            int workedDays,
            int vacationDays,
            double workHours,
            double overtimeHours,
            double dailyRate,
            double overtimePay,
            double netSalary,
            Date paymentDate
    ) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.grossSalary = grossSalary;
        this.bonus = bonus;
        this.deductions = deductions;
        this.workedDays = workedDays;
        this.vacationDays = vacationDays;
        this.workHours = workHours;
        this.overtimeHours = overtimeHours;
        this.dailyRate = dailyRate;
        this.overtimePay = overtimePay;
        this.netSalary = netSalary;
        this.paymentDate = paymentDate;
    }

    public Salary(
            int id,
            int employeeId,
            double grossSalary,
            double bonus,
            double deductions,
            int workedDays,
            int vacationDays,
            double workHours,
            double overtimeHours,
            double dailyRate,
            double overtimePay,
            double netSalary,
            Date paymentDate
    ) {
        this(
                id,
                employeeId,
                "",
                grossSalary,
                bonus,
                deductions,
                workedDays,
                vacationDays,
                workHours,
                overtimeHours,
                dailyRate,
                overtimePay,
                netSalary,
                paymentDate
        );
    }

    public int getId() {
        return id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public double getGrossSalary() {
        return grossSalary;
    }

    public double getBonus() {
        return bonus;
    }

    public double getDeductions() {
        return deductions;
    }

    public int getWorkedDays() {return workedDays; }

    public int getVacationDays() {
        return vacationDays;
    }

    public double getWorkHours() {
        return workHours;
    }

    public double getOvertimeHours() {
        return overtimeHours;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public double getOvertimePay() {
        return overtimePay;
    }

    public double getNetSalary() {
        return netSalary;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }
}
