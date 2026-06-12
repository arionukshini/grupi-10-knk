package com.company.system.model.dto;

import java.time.LocalDate;

public record LeaveRequestCreateDto(
        int employeeId,
        String type,
        LocalDate startDate,
        LocalDate endDate,
        String reason
) {
}
