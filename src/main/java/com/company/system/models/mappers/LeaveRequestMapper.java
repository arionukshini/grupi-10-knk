package com.company.system.models.mappers;

import com.company.system.models.LeaveRequest;
import com.company.system.models.dto.LeaveRequestResponseDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LeaveRequestMapper implements Mapper<LeaveRequest> {

    @Override
    public LeaveRequest fromResultSet(ResultSet rs) throws SQLException {
        return new LeaveRequest(
                rs.getInt("id"),
                rs.getInt("employee_id"),
                rs.getString("employee_name"),
                rs.getString("request_type"),
                rs.getDate("start_date"),
                rs.getDate("end_date"),
                rs.getString("reason"),
                rs.getString("status"),
                rs.getString("admin_response"),
                rs.getTimestamp("requested_at"),
                rs.getTimestamp("reviewed_at")
        );
    }

    @Override
    public LeaveRequestResponseDto toDto(LeaveRequest leaveRequest) {
        return new LeaveRequestResponseDto(
                leaveRequest.getId(),
                leaveRequest.getEmployeeId(),
                leaveRequest.getEmployeeName(),
                leaveRequest.getRequestType(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate(),
                leaveRequest.getReason(),
                leaveRequest.getStatus(),
                leaveRequest.getAdminResponse(),
                leaveRequest.getRequestedAt(),
                leaveRequest.getReviewedAt(),
                leaveRequest.getWorkingDays()
        );
    }
}
