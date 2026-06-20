package com.company.system.models.dto;

public record PasswordResetRequestDto(String username, String passwordHash) implements IRequestDto {
}
