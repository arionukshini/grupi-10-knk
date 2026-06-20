package com.company.system.models.dto;

import java.sql.Timestamp;

public record UserResponseDto(
        int id,
        String username,
        Integer employeeId,
        String employeeName,
        String role,
        Timestamp createdAt,
        boolean mustChangePassword
) implements IResponseDto {
}
