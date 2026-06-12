package com.company.system.model.dto;

public record UserRegistrationRequestDto(String username, String passwordHash, String role) {
}
