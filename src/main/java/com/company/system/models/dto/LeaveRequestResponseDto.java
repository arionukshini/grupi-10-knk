package com.company.system.models.dto;

import java.sql.Date;
import java.sql.Timestamp;

public record LeaveRequestResponseDto(
        int id,
        int employeeId,
        String employeeName,
        String requestType,
        Date startDate,
        Date endDate,
        String reason,
        String status,
        String adminResponse,
        Timestamp requestedAt,
        Timestamp reviewedAt,
        int workingDays
) implements IResponseDto {
}
