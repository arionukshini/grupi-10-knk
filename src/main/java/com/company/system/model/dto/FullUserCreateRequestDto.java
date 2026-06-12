package com.company.system.model.dto;

import java.time.LocalDate;

public record FullUserCreateRequestDto(
        String firstName,
        String lastName,
        String email,
        String phone,
        String position,
        int departmentId,
        String employeeStatus,
        String contractType,
        LocalDate startDate,
        LocalDate endDate,
        String contractStatus,
        double gross,
        double bonus,
        double deductions,
        int workHours,
        int vacationDays,
        double overtimeHours,
        String username,
        String tempPassword
) {
}
