package com.company.system.models.dto;

public record LeaveRequestDecisionDto(int requestId, String status, String adminResponse) implements IRequestDto {
}
