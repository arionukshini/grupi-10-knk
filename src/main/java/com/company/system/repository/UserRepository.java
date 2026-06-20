package com.company.system.repository;

import com.company.system.db.DBConnection;
import com.company.system.models.mappers.UserMapper;
import com.company.system.models.User;
import com.company.system.models.dto.PasswordResetRequestDto;
import com.company.system.models.dto.UserRegistrationRequestDto;
import com.company.system.models.dto.UserUpdateRequestDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository extends BaseRepository<User> {

    private final UserMapper mapper = new UserMapper();
    public User findByUsername(String username) throws SQLException {
        String sql = """
                SELECT u.id,
                       u.employee_id,
                       CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
                       u.username,
                       u.password_hash,
                       u.role,
                       u.created_at,
                       u.must_change_password
                FROM users u
                LEFT JOIN employees e ON u.employee_id = e.id
                WHERE u.username = ?
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, username.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.fromResultSet(rs);
                }
            }
        }

        return null;
    }
    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = """
                SELECT u.id,
                       u.employee_id,
                       CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
                       u.username,
                       u.password_hash,
                       u.role,
                       u.created_at,
                       u.must_change_password
                FROM users u
                LEFT JOIN employees e ON u.employee_id = e.id
                ORDER BY u.id
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                users.add(mapper.fromResultSet(rs));
            }
        }

        return users;
    }
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, username.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    public boolean save(UserRegistrationRequestDto request) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, request.username().trim());
            stmt.setString(2, request.passwordHash());
            stmt.setString(3, request.role());
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean update(UserUpdateRequestDto request) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE users SET username = ?");
        boolean changePassword = request.passwordHash() != null && !request.passwordHash().isBlank();

        if (changePassword) {
            sql.append(", password_hash = ?");
        }
        if (request.employeeId() != null) {
            sql.append(", employee_id = ?");
        } else {
            sql.append(", employee_id = NULL");
        }
        sql.append(" WHERE id = ?");

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())
        ) {
            int index = 1;
            stmt.setString(index++, request.username());
            if (changePassword) {
                stmt.setString(index++, request.passwordHash());
            }
            if (request.employeeId() != null) {
                stmt.setInt(index++, request.employeeId());
            }
            stmt.setInt(index, request.userId());
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean updatePasswordByUsername(PasswordResetRequestDto request) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, must_change_password = FALSE WHERE username = ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, request.passwordHash());
            stmt.setString(2, request.username().trim());
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean updatePasswordByUserId(int userId, String passwordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, must_change_password = FALSE WHERE id = ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, passwordHash);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    public String findPasswordHashByUsername(String username) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        return findSingleString(sql, username);
    }
    public String findPasswordHashByUsernameAndEmail(String username, String email) throws SQLException {
        String sql = """
                SELECT u.password_hash
                FROM users u
                JOIN employees e ON u.employee_id = e.id
                WHERE u.username = ? AND LOWER(e.email) = LOWER(?)
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, username.trim());
            stmt.setString(2, email.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        }

        return null;
    }
    public String findEmployeeNameById(Integer employeeId) throws SQLException {
        if (employeeId == null || employeeId <= 0) {
            return null;
        }

        String sql = "SELECT CONCAT(first_name, ' ', last_name) AS employee_name FROM employees WHERE id = ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("employee_name");
                }
            }
        }

        return null;
    }
    public int findEmployeeIdByUserId(int userId) throws SQLException {
        String sql = "SELECT employee_id FROM users WHERE id = ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("employee_id");
                }
            }
        }

        return 0;
    }
    @Override
    public boolean deleteById(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean deleteAccountAndEmployeeData(User user) throws SQLException {
        String userSql = "SELECT employee_id, role FROM users WHERE id = ?";
        String adminCountSql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
        String deleteSalaryHistorySql = """
                DELETE FROM salary_history
                WHERE employee_id = ?
                   OR salary_id IN (
                       SELECT id
                       FROM salaries
                       WHERE employee_id = ?
                   )
                """;
        String deleteSalariesSql = "DELETE FROM salaries WHERE employee_id = ?";
        String deleteContractsSql = "DELETE FROM contracts WHERE employee_id = ?";
        String deleteVacationRequestsSql = "DELETE FROM vacation_requests WHERE employee_id = ?";
        String deleteUserSql = "DELETE FROM users WHERE id = ?";
        String deleteEmployeeSql = "DELETE FROM employees WHERE id = ?";

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            try (
                    PreparedStatement userStmt = conn.prepareStatement(userSql);
                    PreparedStatement adminCountStmt = conn.prepareStatement(adminCountSql);
                    PreparedStatement historyStmt = conn.prepareStatement(deleteSalaryHistorySql);
                    PreparedStatement salariesStmt = conn.prepareStatement(deleteSalariesSql);
                    PreparedStatement contractsStmt = conn.prepareStatement(deleteContractsSql);
                    PreparedStatement vacationRequestsStmt = conn.prepareStatement(deleteVacationRequestsSql);
                    PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSql);
                    PreparedStatement deleteEmployeeStmt = conn.prepareStatement(deleteEmployeeSql)
            ) {
                userStmt.setInt(1, user.getId());

                Integer employeeId;
                String role;
                try (ResultSet userRs = userStmt.executeQuery()) {
                    if (!userRs.next()) {
                        conn.rollback();
                        return false;
                    }

                    Object employeeIdValue = userRs.getObject("employee_id");
                    employeeId = employeeIdValue == null ? null : ((Number) employeeIdValue).intValue();
                    role = userRs.getString("role");
                }

                if ("ADMIN".equalsIgnoreCase(role)) {
                    try (ResultSet adminRs = adminCountStmt.executeQuery()) {
                        adminRs.next();

                        if (adminRs.getInt(1) <= 1) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                deleteUserStmt.setInt(1, user.getId());
                boolean deletedUser = deleteUserStmt.executeUpdate() > 0;

                if (!deletedUser) {
                    conn.rollback();
                    return false;
                }

                if (employeeId != null) {
                    historyStmt.setInt(1, employeeId);
                    historyStmt.setInt(2, employeeId);
                    historyStmt.executeUpdate();

                    salariesStmt.setInt(1, employeeId);
                    salariesStmt.executeUpdate();

                    contractsStmt.setInt(1, employeeId);
                    contractsStmt.executeUpdate();

                    vacationRequestsStmt.setInt(1, employeeId);
                    vacationRequestsStmt.executeUpdate();

                    deleteEmployeeStmt.setInt(1, employeeId);
                    deleteEmployeeStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    public int countAdmins() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";

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
    public boolean isEmployeeIdUnique(int employeeId, int excludeUserId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE employee_id = ? AND id != ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, employeeId);
            stmt.setInt(2, excludeUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next();
            }
        }
    }

    private String findSingleString(String sql, String value) throws SQLException {
        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, value.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }

        return null;
    }
}
