package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.LeaveRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestService {

    public static List<LeaveRequest> getAllRequests() {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = """
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
                ORDER BY
                    CASE vr.status
                        WHEN 'Pending' THEN 0
                        WHEN 'Approved' THEN 1
                        ELSE 2
                    END,
                    vr.requested_at DESC
                """;

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                return requests;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRequest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return requests;
    }

    public static int getPendingCount() {
        String sql = "SELECT COUNT(*) FROM vacation_requests WHERE status = 'Pending'";

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                return 0;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean updateStatus(int requestId, String status, String adminResponse) {
        String sql = """
                UPDATE vacation_requests
                SET status = ?,
                    admin_response = ?,
                    reviewed_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status);
                stmt.setString(2, adminResponse == null || adminResponse.isBlank() ? null : adminResponse.trim());
                stmt.setInt(3, requestId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static LeaveRequest mapRequest(ResultSet rs) throws SQLException {
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
}
