package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.Salary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryService {

    public static List<Salary> getAllSalaries() {

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

                salaries.add(new Salary(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getString("employee_name"),
                        rs.getDouble("gross_salary"),
                        rs.getDouble("bonus"),
                        rs.getDouble("deductions"),
                        0,
                        rs.getInt("vacation_days"),
                        rs.getDouble("work_hours"),
                        rs.getDouble("overtime_hours"),
                        rs.getDouble("daily_rate"),
                        rs.getDouble("overtime_pay"),
                        rs.getDouble("net_salary"),
                        rs.getDate("payment_date")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salaries;
    }

    public static boolean addSalary(Salary salary) {

        String sql = """
                INSERT INTO salaries
                (
                    employee_id,
                    gross_salary,
                    bonus,
                    deductions,
                    vacation_days,
                    work_hours,
                    overtime_hours,
                    daily_rate,
                    overtime_pay,
                    net_salary,
                    payment_date
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, salary.getEmployeeId());
            stmt.setDouble(2, salary.getGrossSalary());
            stmt.setDouble(3, salary.getBonus());
            stmt.setDouble(4, salary.getDeductions());
            stmt.setInt(5, salary.getVacationDays());
            stmt.setDouble(6, salary.getWorkHours());
            stmt.setDouble(7, salary.getOvertimeHours());
            stmt.setDouble(8, salary.getDailyRate());
            stmt.setDouble(9, salary.getOvertimePay());
            stmt.setDouble(10, salary.getNetSalary());
            stmt.setDate(11, salary.getPaymentDate());

            int affected = stmt.executeUpdate();

            if (affected > 0) {

                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int salaryId = keys.getInt(1);

                    insertHistory(conn, salary, salaryId);
                }

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateSalary(Salary salary) {

        String sql = """
                UPDATE salaries
                SET
                    employee_id = ?,
                    gross_salary = ?,
                    bonus = ?,
                    deductions = ?,
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

            stmt.setInt(1, salary.getEmployeeId());
            stmt.setDouble(2, salary.getGrossSalary());
            stmt.setDouble(3, salary.getBonus());
            stmt.setDouble(4, salary.getDeductions());
            stmt.setInt(5, salary.getVacationDays());
            stmt.setDouble(6, salary.getWorkHours());
            stmt.setDouble(7, salary.getOvertimeHours());
            stmt.setDouble(8, salary.getDailyRate());
            stmt.setDouble(9, salary.getOvertimePay());
            stmt.setDouble(10, salary.getNetSalary());
            stmt.setDate(11, salary.getPaymentDate());
            stmt.setInt(12, salary.getId());

            int updated = stmt.executeUpdate();

            if (updated > 0) {
                insertHistory(conn, salary, salary.getId());
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteSalary(int salaryId) {

        String deleteHistorySql = "DELETE FROM salary_history WHERE salary_id = ?";
        String deleteSalarySql = "DELETE FROM salaries WHERE id = ?";

        try (Connection conn = DBConnection.connect()) {

            try (PreparedStatement stmt1 = conn.prepareStatement(deleteHistorySql)) {
                stmt1.setInt(1, salaryId);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(deleteSalarySql)) {
                stmt2.setInt(1, salaryId);
                return stmt2.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Salary calculateSalary(
            int id,
            int employeeId,
            double monthlySalary,
            int workedDays,
            int vacationDays,
            double workHours,
            double overtimeHours,
            double bonus,
            double deductions,
            Date paymentDate
    ) {

        double dailyRate = monthlySalary / 22;

        double overtimePay = overtimeHours * (dailyRate / 8) * 1.5;

        double grossSalary = monthlySalary + overtimePay;

        double netSalary = grossSalary + bonus - deductions;

        return new Salary(
                id,
                employeeId,
                grossSalary,
                bonus,
                deductions,
                workedDays,
                vacationDays,
                workHours,
                overtimeHours,
                dailyRate,
                overtimePay,
                netSalary,
                paymentDate
        );
    }

    public static void addSalaryHistory(Salary salary) {

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

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salary.getId());
            stmt.setInt(2, salary.getEmployeeId());
            stmt.setDouble(3, salary.getGrossSalary());
            stmt.setDouble(4, salary.getBonus());
            stmt.setDouble(5, salary.getDeductions());
            stmt.setDouble(6, salary.getNetSalary());
            stmt.setDate(7, salary.getPaymentDate());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Salary> getSalaryHistory(int employeeId) {

        List<Salary> salaries = new ArrayList<>();

        String sql = """
                SELECT *
                FROM salary_history
                WHERE employee_id = ?
                ORDER BY payment_date DESC
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Salary salary = new Salary(
                        0, // id not needed for history view
                        rs.getInt("employee_id"),
                        rs.getDouble("gross_salary"),
                        rs.getDouble("bonus"),
                        rs.getDouble("deductions"),
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        rs.getDouble("net_salary"),
                        rs.getDate("payment_date")
                );

                salaries.add(salary);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salaries;
    }

    private static void insertHistory(Connection conn, Salary salary, int salaryId) {

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

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salaryId);
            stmt.setInt(2, salary.getEmployeeId());
            stmt.setDouble(3, salary.getGrossSalary());
            stmt.setDouble(4, salary.getBonus());
            stmt.setDouble(5, salary.getDeductions());
            stmt.setDouble(6, salary.getNetSalary());
            stmt.setDate(7, salary.getPaymentDate());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
