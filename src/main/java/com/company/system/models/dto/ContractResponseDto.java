package com.company.system.models.dto;

import java.sql.Date;

public record ContractResponseDto(
        int id,
        int employeeId,
        String employeeName,
        String contractType,
        Date startDate,
        Date endDate,
        double salary,
        String status
) implements IResponseDto {
}
