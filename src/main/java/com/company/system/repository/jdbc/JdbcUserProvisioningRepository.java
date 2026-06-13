package com.company.system.repository.jdbc;

import com.company.system.db.DBConnection;
import com.company.system.model.dto.FullUserCreateRequestDto;
import com.company.system.repository.UserProvisioningRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class JdbcUserProvisioningRepository implements UserProvisioningRepository {

    @Override
    public boolean createFullUser(FullUserCreateRequestDto request, String passwordHash) throws SQLException {
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
            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            try {
                int employeeId;
                try (PreparedStatement stmt = conn.prepareStatement(insertEmployee, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, request.firstName());
                    stmt.setString(2, request.lastName());
                    stmt.setString(3, request.email());
                    stmt.setString(4, request.phone());
                    stmt.setString(5, request.position());
                    stmt.setInt(6, request.departmentId());
                    stmt.setDate(7, Date.valueOf(request.startDate()));
                    stmt.setDouble(8, request.gross());
                    stmt.setString(9, request.employeeStatus());
                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            conn.rollback();
                            return false;
                        }
                        employeeId = keys.getInt(1);
                    }
                }

                try (PreparedStatement stmt = conn.prepareStatement(insertContract)) {
                    stmt.setInt(1, employeeId);
                    stmt.setString(2, request.contractType());
                    stmt.setDate(3, Date.valueOf(request.startDate()));
                    stmt.setDate(4, request.endDate() != null ? Date.valueOf(request.endDate()) : null);
                    stmt.setDouble(5, request.gross());
                    stmt.setString(6, request.contractStatus());
                    stmt.executeUpdate();
                }

                double dailyRate = request.gross() > 0 ? request.gross() / 22.0 : 0;
                double hourlyRate = request.workHours() > 0 ? request.gross() / request.workHours() : 0;
                double overtimePay = hourlyRate * 1.5 * request.overtimeHours();
                double netSalary = request.gross() + request.bonus() + overtimePay - request.deductions();

                try (PreparedStatement stmt = conn.prepareStatement(insertSalary)) {
                    stmt.setInt(1, employeeId);
                    stmt.setDouble(2, request.gross());
                    stmt.setDouble(3, request.bonus());
                    stmt.setDouble(4, request.deductions());
                    stmt.setDouble(5, request.workHours());
                    stmt.setInt(6, request.vacationDays());
                    stmt.setDouble(7, request.overtimeHours());
                    stmt.setDouble(8, dailyRate);
                    stmt.setDouble(9, overtimePay);
                    stmt.setDouble(10, netSalary);
                    stmt.setDate(11, Date.valueOf(LocalDate.now()));
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(insertUser)) {
                    stmt.setString(1, request.username());
                    stmt.setString(2, passwordHash);
                    stmt.setInt(3, employeeId);
                    stmt.executeUpdate();
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
}
