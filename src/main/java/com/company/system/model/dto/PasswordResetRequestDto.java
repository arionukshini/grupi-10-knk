package com.company.system.model.dto;

public record PasswordResetRequestDto(String username, String passwordHash) {
}
