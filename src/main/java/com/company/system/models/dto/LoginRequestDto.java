package com.company.system.models.dto;

public record LoginRequestDto(String username, String password) implements IRequestDto {
}
