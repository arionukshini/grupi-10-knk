package com.company.system.service;


import com.company.system.utils.AppLogger;
import com.company.system.model.LeaveRequest;
import com.company.system.model.dto.LeaveRequestCreateDto;
import com.company.system.model.dto.LeaveRequestDecisionDto;
import com.company.system.repository.LeaveRequestRepository;
import com.company.system.repository.jdbc.JdbcLeaveRequestRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestService {

    private static final LeaveRequestRepository leaveRequestRepository = new JdbcLeaveRequestRepository();

    public static List<LeaveRequest> getAllRequests() {
        try {
            return leaveRequestRepository.findAll();
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return new ArrayList<>();
        }
    }

    public static int getPendingCount() {
        try {
            return leaveRequestRepository.countPending();
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return 0;
        }
    }

    public static boolean updateStatus(int requestId, String status, String adminResponse) {
        LeaveRequestDecisionDto request = new LeaveRequestDecisionDto(requestId, status, adminResponse);
        try {
            return leaveRequestRepository.updateStatus(request);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return false;
        }
    }

    public static List<LeaveRequest> getRequestsByEmployee(int employeeId) {
        try {
            return leaveRequestRepository.findByEmployeeId(employeeId);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return new ArrayList<>();
        }
    }

    public static boolean submitRequest(int employeeId, String type, LocalDate startDate, LocalDate endDate, String reason) {
        LeaveRequestCreateDto request = new LeaveRequestCreateDto(employeeId, type, startDate, endDate, reason);
        try {
            return leaveRequestRepository.save(request);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return false;
        }
    }

    public static boolean cancelRequest(int requestId) {
        try {
            return leaveRequestRepository.deletePendingById(requestId);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return false;
        }
    }
}
