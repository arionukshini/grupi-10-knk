package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.Salary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SalaryService {

    public static List<Salary> getAllSalaries() {

        List<Salary> salaries = new ArrayList<>();

        String sql = "SELECT * FROM salaries ORDER BY payment_date DESC";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                Salary salary = new Salary(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getDouble("amount"),
                        rs.getDouble("bonus"),
                        rs.getDouble("deductions"),
                        rs.getInt("vacation_days"),
                        rs.getDouble("work_hours"),
                        rs.getDouble("overtime_hours"),
                        rs.getDouble("daily_rate"),
                        rs.getDouble("overtime_pay"),
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

    public static void addSalary(Salary salary) {

        String sql = """
        INSERT INTO salaries
        (
            employee_id,
            amount,
            bonus,
            deductions,
            net_salary,
            vacation_days,
            work_hours,
            overtime_hours,
            daily_rate,
            overtime_pay,
            payment_date
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, salary.getEmployeeId());
            stmt.setDouble(2, salary.getGrossSalary());
            stmt.setDouble(3, salary.getBonus());
            stmt.setDouble(4, salary.getDeductions());
            stmt.setDouble(5, salary.getNetSalary());
            stmt.setInt(6, salary.getVacationDays());
            stmt.setDouble(7, salary.getWorkHours());
            stmt.setDouble(8, salary.getOvertimeHours());
            stmt.setDouble(9, salary.getDailyRate());
            stmt.setDouble(10, salary.getOvertimePay());
            stmt.setDate(11, salary.getPaymentDate());

            stmt.executeUpdate();


            System.out.println("Salary added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static List<Salary> getSalaryHistory(int employeeId) {

        List<Salary> salaries = new ArrayList<>();

        String sql = """
            SELECT *
            FROM salaries
            WHERE employee_id = ?
            ORDER BY payment_date DESC
            LIMIT 3
            """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, employeeId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Salary salary = new Salary(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getDouble("amount"),
                        rs.getDouble("bonus"),
                        rs.getDouble("deductions"),
                        rs.getInt("vacation_days"),
                        rs.getDouble("work_hours"),
                        rs.getDouble("overtime_hours"),
                        rs.getDouble("daily_rate"),
                        rs.getDouble("overtime_pay"),
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
    public static boolean updateSalary(Salary salary) {

        String sql = """
            UPDATE salaries
            SET
                employee_id = ?,
                amount = ?,
                bonus = ?,
                deductions = ?,
                net_salary = ?,
                vacation_days = ?,
                work_hours = ?,
                overtime_hours = ?,
                daily_rate = ?,
                overtime_pay = ?,
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
            stmt.setDouble(5, salary.getNetSalary());
            stmt.setInt(6, salary.getVacationDays());
            stmt.setDouble(7, salary.getWorkHours());
            stmt.setDouble(8, salary.getOvertimeHours());
            stmt.setDouble(9, salary.getDailyRate());
            stmt.setDouble(10, salary.getOvertimePay());
            stmt.setDate(11, salary.getPaymentDate());
            stmt.setInt(12, salary.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public static boolean deleteSalary(int salaryId) {

        String sql = "DELETE FROM salaries WHERE id = ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, salaryId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public static Salary calculateSalary(
            int id,
            int employeeId,
            double monthlySalary,
            int vacationDays,
            double workHours,
            double overtimeHours,
            double bonus,
            double deductions,
            Date paymentDate
    ) {

        double dailyRate = monthlySalary / 22;

        double overtimePay =
                overtimeHours * (dailyRate / 8) * 1.5;

        double grossSalary =
                monthlySalary + overtimePay;

        double netSalary =
                grossSalary + bonus - deductions;

        return new Salary(
                id,
                employeeId,
                grossSalary,
                bonus,
                deductions,
                vacationDays,
                workHours,
                overtimeHours,
                dailyRate,
                overtimePay,
                netSalary,
                paymentDate
        );
    }
}