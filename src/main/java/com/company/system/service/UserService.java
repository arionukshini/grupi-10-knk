package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.User;
import com.company.system.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    public static User login(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? ";

        try (Connection conn = DBConnection.connect()) {

            if (conn == null || username == null || username.isBlank() || password == null || password.isBlank()) {
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username.trim());

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");

                    if (PasswordUtils.verifyPassword(password, storedHash)) {
                        Object employeeIdValue = rs.getObject("employee_id");
                        Integer employeeId = employeeIdValue == null ? null : ((Number) employeeIdValue).intValue();

                        return new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                employeeId,
                                getEmployeeName(employeeId, conn),
                                storedHash,
                                rs.getString("role"),
                                rs.getTimestamp("created_at")
                        );
                    }

                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean register(String username, String password) {

        String sql ="INSERT INTO users (username,password_hash, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.connect()) {

            if (conn == null || username == null || username.isBlank() || password == null || password.isBlank()) {
                return false;
            }
            if (userExists(username, conn)) {
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username.trim());
                stmt.setString(2, PasswordUtils.hashPassword(password));
                stmt.setString(3, "USER");

                return stmt.executeUpdate() > 0;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = """
                SELECT u.id,
                       u.employee_id,
                       CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
                       u.username,
                       u.password_hash,
                       u.role,
                       u.created_at
                FROM users u
                LEFT JOIN employees e ON u.employee_id = e.id
                ORDER BY u.id
                """;

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                return users;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Object employeeIdValue = rs.getObject("employee_id");
                    Integer employeeId = employeeIdValue == null ? null : ((Number) employeeIdValue).intValue();

                    users.add(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            employeeId,
                            rs.getString("employee_name"),
                            rs.getString("password_hash"),
                            rs.getString("role"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static String getDisplayName(User user) {
        if (user == null) {
            return "-";
        }

        if (user.getEmployeeName() != null && !user.getEmployeeName().isBlank()) {
            return user.getEmployeeName();
        }

        if (user.getEmployeeId() == null) {
            return user.getUsername();
        }

        String sql = "SELECT CONCAT(first_name, ' ', last_name) AS employee_name FROM employees WHERE id = ?";

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                return user.getUsername();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, user.getEmployeeId());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String employeeName = rs.getString("employee_name");

                        if (employeeName != null && !employeeName.isBlank()) {
                            return employeeName;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user.getUsername();
    }

    public static boolean resetPassword(String username, String newPassword) {

        String sql = "UPDATE users SET password_hash = ? WHERE username = ?";

        try (Connection conn = DBConnection.connect()) {

            if (conn == null || username == null || username.isBlank() || newPassword == null || newPassword.isBlank()) {
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, PasswordUtils.hashPassword(newPassword));
                stmt.setString(2, username.trim());

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
        public static String getPasswordHashByUsername(String username) {

            String sql = "SELECT password_hash FROM users WHERE username = ?";

            try (Connection conn = DBConnection.connect()) {

                if (conn == null || username == null || username.isBlank()) {
                    return null;
                }

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username.trim());

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        return rs.getString("password_hash");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }
    public static boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DBConnection.connect()) {

            if (conn == null || username == null || username.isBlank()) {
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username.trim());

                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.connect()) {

            if (conn == null) {
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isOnlyAdmin(User user) {
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                return true;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) <= 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean deleteAccountAndEmployeeData(User user) {
        if (user == null) {
            return false;
        }

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
                    PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSql);
                    PreparedStatement deleteEmployeeStmt = conn.prepareStatement(deleteEmployeeSql)
            ) {
                userStmt.setInt(1, user.getId());

                Integer employeeId = null;
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

                    deleteEmployeeStmt.setInt(1, employeeId);
                    deleteEmployeeStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

        private static boolean userExists(String username, Connection conn) throws SQLException {
            String sql = "SELECT 1 FROM users WHERE username = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username.trim());

                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        }

        private static String getEmployeeName(Integer employeeId, Connection conn) throws SQLException {
            if (employeeId == null || employeeId <= 0) {
                return null;
            }

            String sql = "SELECT CONCAT(first_name, ' ', last_name) AS employee_name FROM employees WHERE id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, employeeId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("employee_name");
                    }
                }
            }

            return null;
        }
}
