package com.company.system.models.dto;

import java.sql.Date;

public record SalaryCalculationRequestDto(
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
) implements IRequestDto {
}
