package com.company.system.repository;

import com.company.system.db.DBConnection;
import com.company.system.models.mappers.SalaryHistoryMapper;
import com.company.system.models.mappers.SalaryMapper;
import com.company.system.models.Salary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SalaryRepository extends BaseRepository<Salary> {

    private final SalaryMapper mapper = new SalaryMapper();
    private final SalaryHistoryMapper historyMapper = new SalaryHistoryMapper();
    @Override
    public List<Salary> findAll() throws SQLException {
        List<Salary> salaries = new ArrayList<>();
        String sql = """
                SELECT s.*, CONCAT(e.first_name, ' ', e.last_name) AS employee_name
                FROM salaries s
                JOIN employees e ON s.employee_id = e.id
                ORDER BY s.payment_date DESC
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                salaries.add(mapper.fromResultSet(rs));
            }
        }

        return salaries;
    }
    public Salary findLatestByEmployeeId(int employeeId) throws SQLException {
        String sql = """
                SELECT s.*, CONCAT(e.first_name, ' ', e.last_name) AS employee_name
                FROM salaries s
                JOIN employees e ON s.employee_id = e.id
                WHERE s.employee_id = ?
                ORDER BY s.payment_date DESC, s.id DESC
                LIMIT 1
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.fromResultSet(rs);
                }
            }
        }

        return null;
    }
    public List<Salary> findHistoryByEmployeeId(int employeeId) throws SQLException {
        List<Salary> salaries = new ArrayList<>();
        String sql = """
                SELECT *
                FROM salary_history
                WHERE employee_id = ?
                ORDER BY payment_date DESC
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    salaries.add(historyMapper.fromResultSet(rs));
                }
            }
        }

        return salaries;
    }
    @Override
    public boolean save(Salary salary) throws SQLException {
        String sql = """
                INSERT INTO salaries
                (
                    employee_id,
                    gross_salary,
                    bonus,
                    deductions,
                    worked_days,
                    vacation_days,
                    work_hours,
                    overtime_hours,
                    daily_rate,
                    overtime_pay,
                    net_salary,
                    payment_date
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            bindSalary(stmt, salary);
            return stmt.executeUpdate() > 0;
        }
    }
    @Override
    public boolean update(Salary salary) throws SQLException {
        String sql = """
                UPDATE salaries
                SET
                    employee_id = ?,
                    gross_salary = ?,
                    bonus = ?,
                    deductions = ?,
                    worked_days = ?,
                    vacation_days = ?,
                    work_hours = ?,
                    overtime_hours = ?,
                    daily_rate = ?,
                    overtime_pay = ?,
                    net_salary = ?,
                    payment_date = ?
                WHERE id = ?
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            bindSalary(stmt, salary);
            stmt.setInt(13, salary.getId());
            return stmt.executeUpdate() > 0;
        }
    }
    @Override
    public boolean deleteById(int salaryId) throws SQLException {
        String deleteHistorySql = "DELETE FROM salary_history WHERE salary_id = ?";
        String deleteSalarySql = "DELETE FROM salaries WHERE id = ?";

        try (Connection conn = DBConnection.connect()) {
            try (PreparedStatement stmt = conn.prepareStatement(deleteHistorySql)) {
                stmt.setInt(1, salaryId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(deleteSalarySql)) {
                stmt.setInt(1, salaryId);
                return stmt.executeUpdate() > 0;
            }
        }
    }
    public boolean saveHistory(Salary salary) throws SQLException {
        String sql = """
                INSERT INTO salary_history
                (
                    salary_id,
                    employee_id,
                    gross_salary,
                    bonus,
                    deductions,
                    net_salary,
                    payment_date
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, salary.getId());
            stmt.setInt(2, salary.getEmployeeId());
            stmt.setDouble(3, salary.getGrossSalary());
            stmt.setDouble(4, salary.getBonus());
            stmt.setDouble(5, salary.getDeductions());
            stmt.setDouble(6, salary.getNetSalary());
            stmt.setDate(7, salary.getPaymentDate());
            return stmt.executeUpdate() > 0;
        }
    }

    private void bindSalary(PreparedStatement stmt, Salary salary) throws SQLException {
        stmt.setInt(1, salary.getEmployeeId());
        stmt.setDouble(2, salary.getGrossSalary());
        stmt.setDouble(3, salary.getBonus());
        stmt.setDouble(4, salary.getDeductions());
        stmt.setInt(5, salary.getWorkedDays());
        stmt.setInt(6, salary.getVacationDays());
        stmt.setDouble(7, salary.getWorkHours());
        stmt.setDouble(8, salary.getOvertimeHours());
        stmt.setDouble(9, salary.getDailyRate());
        stmt.setDouble(10, salary.getOvertimePay());
        stmt.setDouble(11, salary.getNetSalary());
        stmt.setDate(12, salary.getPaymentDate());
    }
}
