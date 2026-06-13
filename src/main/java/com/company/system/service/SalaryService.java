package com.company.system.service;


import com.company.system.utils.AppLogger;
import com.company.system.exceptions.DatabaseOperationException;
import com.company.system.exceptions.InvalidPaymentException;
import com.company.system.exceptions.InvalidSalaryException;
import com.company.system.model.Salary;
import com.company.system.model.dto.SalaryCalculationRequestDto;
import com.company.system.repository.SalaryRepository;
import com.company.system.repository.jdbc.JdbcSalaryRepository;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalaryService {

    private static final double STANDARD_WORK_DAYS = 22.0;
    private static final double STANDARD_WORK_HOURS = 8.0;
    private static final SalaryRepository salaryRepository = new JdbcSalaryRepository();

    public static List<Salary> getAllSalaries() {
        try {
            return salaryRepository.findAll();
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return new ArrayList<>();
        }
    }

    public static Salary getLatestSalaryByEmployeeId(int employeeId) {
        try {
            return salaryRepository.findLatestByEmployeeId(employeeId);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return null;
        }
    }

    public static boolean addSalary(Salary salary) {
        try {
            return salaryRepository.save(salary);
        } catch (SQLException e) {
            throw new DatabaseOperationException("exception.salary.save.database", e);
        }
    }

    public static boolean updateSalary(Salary salary) {
        try {
            boolean updated = salaryRepository.update(salary);
            if (!updated) {
                throw new DatabaseOperationException("exception.salary.update.notFound");
            }
            return true;
        } catch (SQLException e) {
            throw new DatabaseOperationException("exception.salary.update.database", e);
        }
    }

    public static boolean deleteSalary(int salaryId) {
        try {
            return salaryRepository.deleteById(salaryId);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return false;
        }
    }

    public static Salary calculateSalary(
            int id,
            int employeeId,
            double monthlySalary,
            int workedDays,
            int vacationDays,
            double workHours,
            double overtimeHours,
            double bonus,
            double deductions,
            Date paymentDate
    ) {
        SalaryCalculationRequestDto request = new SalaryCalculationRequestDto(
                id,
                employeeId,
                monthlySalary,
                workedDays,
                vacationDays,
                workHours,
                overtimeHours,
                bonus,
                deductions,
                paymentDate
        );

        return calculateSalary(request);
    }

    public static boolean addSalaryHistory(Salary salary) {
        try {
            boolean inserted = salaryRepository.saveHistory(salary);
            if (!inserted) {
                throw new DatabaseOperationException("exception.payment.history.notInserted");
            }
            return true;
        } catch (SQLException e) {
            throw new DatabaseOperationException("exception.payment.history.database", e);
        }
    }

    public static List<Salary> getSalaryHistory(int employeeId) {
        try {
            return salaryRepository.findHistoryByEmployeeId(employeeId);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return new ArrayList<>();
        }
    }

    private static Salary calculateSalary(SalaryCalculationRequestDto request) {
        validatePaymentInput(request);

        double dailyRate = request.monthlySalary() / STANDARD_WORK_DAYS;
        int paidDays = Math.max(0, Math.min((request.workedDays() + request.vacationDays()), (int) STANDARD_WORK_DAYS));
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
