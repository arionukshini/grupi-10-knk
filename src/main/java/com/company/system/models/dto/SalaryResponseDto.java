package com.company.system.models.dto;

import java.sql.Date;

public record SalaryResponseDto(
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
) implements IResponseDto {
}
