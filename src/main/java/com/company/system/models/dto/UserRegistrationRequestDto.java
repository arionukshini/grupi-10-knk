package com.company.system.models.dto;

public record UserRegistrationRequestDto(String username, String passwordHash, String role) implements IRequestDto {
}
