package com.company.system.model.dto;

public record UserUpdateRequestDto(int userId, Integer employeeId, String username, String passwordHash) {
}
