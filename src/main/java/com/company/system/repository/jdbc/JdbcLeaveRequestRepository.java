package com.company.system.repository.jdbc;

import com.company.system.db.DBConnection;
import com.company.system.mapper.LeaveRequestMapper;
import com.company.system.model.LeaveRequest;
import com.company.system.model.dto.LeaveRequestCreateDto;
import com.company.system.model.dto.LeaveRequestDecisionDto;
import com.company.system.repository.LeaveRequestRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcLeaveRequestRepository implements LeaveRequestRepository {

    private static final String SELECT_REQUESTS = """
            SELECT vr.id,
                   vr.employee_id,
                   CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
                   vr.request_type,
                   vr.start_date,
                   vr.end_date,
                   vr.reason,
                   vr.status,
                   vr.admin_response,
                   vr.requested_at,
                   vr.reviewed_at
            FROM vacation_requests vr
            JOIN employees e ON vr.employee_id = e.id
            """;

    private static final String ORDER_REQUESTS = """
            ORDER BY
                CASE vr.status
                    WHEN 'Pending' THEN 0
                    WHEN 'Approved' THEN 1
                    ELSE 2
                END,
                vr.requested_at DESC
            """;

    private final LeaveRequestMapper mapper = new LeaveRequestMapper();

    @Override
    public List<LeaveRequest> findAll() throws SQLException {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = SELECT_REQUESTS + ORDER_REQUESTS;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                requests.add(mapper.fromResultSet(rs));
            }
        }

        return requests;
    }

    @Override
    public List<LeaveRequest> findByEmployeeId(int employeeId) throws SQLException {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = SELECT_REQUESTS + " WHERE vr.employee_id = ? " + ORDER_REQUESTS;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapper.fromResultSet(rs));
                }
            }
        }

        return requests;
    }

    @Override
    public int countPending() throws SQLException {
        String sql = "SELECT COUNT(*) FROM vacation_requests WHERE status = 'Pending'";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    @Override
    public boolean updateStatus(LeaveRequestDecisionDto request) throws SQLException {
        String sql = """
                UPDATE vacation_requests
                SET status = ?,
                    admin_response = ?,
                    reviewed_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, request.status());
            stmt.setString(2, request.adminResponse() == null || request.adminResponse().isBlank()
                    ? null
                    : request.adminResponse().trim());
            stmt.setInt(3, request.requestId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean save(LeaveRequestCreateDto request) throws SQLException {
        String sql = """
                INSERT INTO vacation_requests
                    (employee_id, request_type, start_date, end_date, reason, status)
                VALUES (?, ?, ?, ?, ?, 'Pending')
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, request.employeeId());
            stmt.setString(2, request.type());
            stmt.setDate(3, Date.valueOf(request.startDate()));
            stmt.setDate(4, Date.valueOf(request.endDate()));
            stmt.setString(5, request.reason() == null || request.reason().isBlank() ? null : request.reason());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deletePendingById(int requestId) throws SQLException {
        String sql = """
                DELETE FROM vacation_requests
                WHERE id = ? AND status = 'Pending'
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, requestId);
            return stmt.executeUpdate() > 0;
        }
    }
}
