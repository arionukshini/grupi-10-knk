package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.utils.PasswordUtils;

import java.sql.*;
import java.time.LocalDate;
import java.security.SecureRandom;


public class WizardUserService {

    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";

    /**
     * Creates employee + contract + salary + user records in a single transaction.
     * Rolls back everything if any step fails.
     */
    public static boolean createFullUser(
            String firstName, String lastName, String email, String phone,
            String position, int departmentId, String empStatus,
            String contractType, LocalDate startDate, LocalDate endDate, String contractStatus,
            double gross, double bonus, double deductions, int workHours,
            int vacationDays, double overtimeHours,
            String username, String tempPassword
    ) {
        String insertEmployee = """
                INSERT INTO employees (first_name, last_name, email, phone, position,
                                       department_id, hire_date, base_salary, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        String insertContract = """
                INSERT INTO contracts (employee_id, contract_type, start_date, end_date, salary, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        String insertSalary = """
                INSERT INTO salaries (employee_id, gross_salary, bonus, deductions,
                                      work_hours, vacation_days, overtime_hours,
                                      daily_rate, overtime_pay, net_salary, payment_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        String insertUser = """
                INSERT INTO users (username, password_hash, role, employee_id, must_change_password)
                VALUES (?, ?, 'USER', ?, TRUE)
                """;

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);

            try {
                // Insert employee
                int employeeId;
                try (PreparedStatement stmt = conn.prepareStatement(insertEmployee, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, firstName);
                    stmt.setString(2, lastName);
                    stmt.setString(3, email);
                    stmt.setString(4, phone);
                    stmt.setString(5, position);
                    stmt.setInt(6, departmentId);
                    stmt.setDate(7, Date.valueOf(startDate));
                    stmt.setDouble(8, gross);
                    stmt.setString(9, empStatus);
                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (!keys.next()) { conn.rollback(); return false; }
                        employeeId = keys.getInt(1);
                    }
                }

                //  Insert contract
                try (PreparedStatement stmt = conn.prepareStatement(insertContract)) {
                    stmt.setInt(1, employeeId);
                    stmt.setString(2, contractType);
                    stmt.setDate(3, Date.valueOf(startDate));
                    stmt.setDate(4, endDate != null ? Date.valueOf(endDate) : null);
                    stmt.setDouble(5, gross);
                    stmt.setString(6, contractStatus);
                    stmt.executeUpdate();
                }

                //  Calculate salary fields
                double dailyRate = gross > 0 ? gross / 22.0 : 0;
                double hourlyRate = (workHours > 0) ? gross / workHours : 0;
                double overtimePay = hourlyRate * 1.5 * overtimeHours;
                double netSalary = gross + bonus + overtimePay - deductions;

                // Insert salary
                try (PreparedStatement stmt = conn.prepareStatement(insertSalary)) {
                    stmt.setInt(1, employeeId);
                    stmt.setDouble(2, gross);
                    stmt.setDouble(3, bonus);
                    stmt.setDouble(4, deductions);
                    stmt.setDouble(5, workHours);
                    stmt.setInt(6, vacationDays);
                    stmt.setDouble(7, overtimeHours);
                    stmt.setDouble(8, dailyRate);
                    stmt.setDouble(9, overtimePay);
                    stmt.setDouble(10, netSalary);
                    stmt.setDate(11, Date.valueOf(LocalDate.now()));
                    stmt.executeUpdate();
                }

                //  Insert user
                String hashedPassword = PasswordUtils.hashPassword(tempPassword);
                try (PreparedStatement stmt = conn.prepareStatement(insertUser)) {
                    stmt.setString(1, username);
                    stmt.setString(2, hashedPassword);
                    stmt.setInt(3, employeeId);
                    stmt.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


     //Generates a unique username like
    public static String generateUniqueUsername(String base) {
        if (!usernameExists(base)) return base;

        int suffix = 2;
        while (true) {
            String candidate = base + suffix;
            if (!usernameExists(candidate)) return candidate;
            suffix++;
        }
    }

    private static boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }


     // Generates a random 8-character temporary password.

    public static String generateTempPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(TEMP_PASSWORD_CHARS.charAt(random.nextInt(TEMP_PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    //Edit User methods

    public static boolean updateUser(int userId, Integer employeeId, String username, String newPassword) {
        StringBuilder sql = new StringBuilder("UPDATE users SET username = ?");
        boolean changePassword = newPassword != null && !newPassword.isBlank();

        if (changePassword) sql.append(", password_hash = ?");
        if (employeeId != null) sql.append(", employee_id = ?");
        else sql.append(", employee_id = NULL");
        sql.append(" WHERE id = ?");

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            stmt.setString(idx++, username);
            if (changePassword) stmt.setString(idx++, PasswordUtils.hashPassword(newPassword));
            if (employeeId != null) stmt.setInt(idx++, employeeId);
            stmt.setInt(idx, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEmployeeIdUnique(int employeeId, int excludeUserId) {
        String sql = "SELECT 1 FROM users WHERE employee_id = ? AND id != ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setInt(2, excludeUserId);
            return !stmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }
}

