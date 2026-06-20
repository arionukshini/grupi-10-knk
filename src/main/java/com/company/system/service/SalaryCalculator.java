package com.company.system.service;

import com.company.system.exceptions.InvalidPaymentException;
import com.company.system.exceptions.InvalidSalaryException;
import com.company.system.models.Salary;
import com.company.system.models.dto.SalaryCalculationRequestDto;

public final class SalaryCalculator {

    static final double STANDARD_WORK_DAYS = 22.0;
    static final double STANDARD_WORK_HOURS = 8.0;

    private SalaryCalculator() {
    }

    public static Salary calculate(SalaryCalculationRequestDto request) {
        validatePaymentInput(request);

        double dailyRate = request.monthlySalary() / STANDARD_WORK_DAYS;
        int paidDays = request.workedDays() + request.vacationDays();
        double basePay = dailyRate * paidDays;
        double overtimePay = request.overtimeHours() * (dailyRate / STANDARD_WORK_HOURS) * 1.5;
        double grossSalary = basePay + overtimePay;
        double netSalary = grossSalary + request.bonus() - request.deductions();

        if (netSalary < 0) {
            throw new InvalidPaymentException("exception.payment.netNegative");
        }

        return new Salary(
                request.id(),
                request.employeeId(),
                grossSalary,
                request.bonus(),
                request.deductions(),
                request.workedDays(),
                request.vacationDays(),
                request.workHours(),
                request.overtimeHours(),
                dailyRate,
                overtimePay,
                netSalary,
                request.paymentDate()
        );
    }

    private static void validatePaymentInput(SalaryCalculationRequestDto request) {
        if (request.employeeId() <= 0) {
            throw new InvalidPaymentException("exception.payment.employeePositive");
        }
        if (request.monthlySalary() <= 0) {
            throw new InvalidSalaryException(request.monthlySalary(), "salaries.baseSalary");
        }
        if (request.workedDays() < 0 || request.workedDays() > STANDARD_WORK_DAYS) {
            throw new InvalidPaymentException("exception.payment.workedDaysRange");
        }
        if (request.vacationDays() < 0 || request.vacationDays() > STANDARD_WORK_DAYS) {
            throw new InvalidPaymentException("exception.payment.vacationDaysRange");
        }
        if (request.workedDays() + request.vacationDays() > STANDARD_WORK_DAYS) {
            throw new InvalidPaymentException("exception.payment.totalDaysRange");
        }
        if (request.workHours() < 0 || request.overtimeHours() < 0) {
            throw new InvalidPaymentException("exception.payment.hoursNegative");
        }
        if (request.bonus() < 0) {
            throw new InvalidSalaryException(request.bonus(), "salaries.bonus");
        }
        if (request.deductions() < 0) {
            throw new InvalidSalaryException(request.deductions(), "salaries.deductions");
        }
        if (request.paymentDate() == null) {
            throw new InvalidPaymentException("exception.payment.dateRequired");
        }
    }
}
