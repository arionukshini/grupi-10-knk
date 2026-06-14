package com.company.system.models.dto;

import java.sql.Date;

public record EmployeeResponseDto(
        int id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String position,
        int departmentId,
        Date hireDate,
        double baseSalary,
        String status
) implements IResponseDto {
}
