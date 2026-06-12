package com.company.system.repository;

import com.company.system.model.LeaveRequest;
import com.company.system.model.dto.LeaveRequestCreateDto;
import com.company.system.model.dto.LeaveRequestDecisionDto;

import java.sql.SQLException;
import java.util.List;

public interface LeaveRequestRepository {

    List<LeaveRequest> findAll() throws SQLException;

    List<LeaveRequest> findByEmployeeId(int employeeId) throws SQLException;

    int countPending() throws SQLException;

    boolean updateStatus(LeaveRequestDecisionDto request) throws SQLException;

    boolean save(LeaveRequestCreateDto request) throws SQLException;

    boolean deletePendingById(int requestId) throws SQLException;
}
