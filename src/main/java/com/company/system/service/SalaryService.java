package com.company.system.service;


import com.company.system.utils.AppLogger;
import com.company.system.exceptions.DatabaseOperationException;
import com.company.system.models.Salary;
import com.company.system.models.dto.SalaryCalculationRequestDto;
import com.company.system.repository.SalaryRepository;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalaryService {

    private static final SalaryRepository salaryRepository = new SalaryRepository();

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

        return SalaryCalculator.calculate(request);
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

}
