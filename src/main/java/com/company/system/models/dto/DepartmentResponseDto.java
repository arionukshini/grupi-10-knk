package com.company.system.models.dto;

public record DepartmentResponseDto(
        int id,
        String name,
        String description
) implements IResponseDto {
}
