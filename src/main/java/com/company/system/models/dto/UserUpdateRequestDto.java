package com.company.system.models.dto;

public record UserUpdateRequestDto(int userId, Integer employeeId, String username, String passwordHash) implements IRequestDto {
}
