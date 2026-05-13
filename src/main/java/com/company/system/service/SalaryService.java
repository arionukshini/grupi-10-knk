package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.Salary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryService {

    public static List<Salary> getAllSalaries() {

        List<Salary> salaries = new ArrayList<>();

        String sql = "SELECT * FROM salaries";

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
                    payment_date
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, salary.getEmployeeId());
            stmt.setDouble(2, salary.getAmount());
            stmt.setDouble(3, salary.getBonus());
            stmt.setDouble(4, salary.getDeductions());
            stmt.setDate(5, salary.getPaymentDate());

            stmt.executeUpdate();

            System.out.println("Salary added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}