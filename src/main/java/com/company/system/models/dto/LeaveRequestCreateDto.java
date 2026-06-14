package com.company.system.models.dto;

import java.time.LocalDate;

public record LeaveRequestCreateDto(
        int employeeId,
        String type,
        LocalDate startDate,
        LocalDate endDate,
        String reason
) implements IRequestDto {
}
