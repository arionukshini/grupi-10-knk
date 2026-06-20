package com.company.system.models.dto;

public record LoginResponseDto(
        boolean success,
        String message,
        UserResponseDto user
) implements IResponseDto {
}
