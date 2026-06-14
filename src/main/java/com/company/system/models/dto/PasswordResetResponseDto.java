package com.company.system.models.dto;

public record PasswordResetResponseDto(
        boolean success,
        String message
) implements IResponseDto {
}
