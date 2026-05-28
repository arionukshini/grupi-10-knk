package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.DashboardStats;
import com.company.system.model.DepartmentStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DashboardService {

    public static DashboardStats getDashboardStats() {
        int totalEmployees = getCount("SELECT COUNT(*) FROM employees");
        int totalContracts = getCount("SELECT COUNT(*) FROM contracts");
        int totalSalaries = getCount("SELECT COUNT(*) FROM salaries");

        int activeContracts = getCount("""
                SELECT COUNT(*)
                FROM contracts
                WHERE status = 'Active'
                """);

        int pendingContracts = getCount("""
                SELECT COUNT(*)
                FROM contracts
                WHERE status = 'Pending'
                """);

        int expiredContracts = getCount("""
                SELECT COUNT(*)
                FROM contracts
                WHERE status = 'Expired'
                """);

        int expiringContracts = getCount("""
                SELECT COUNT(*)
                FROM contracts
                WHERE end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)
                """);

        double averageSalary = getAverage("""
                SELECT AVG(net_salary)
                FROM salaries
                """);

        return new DashboardStats(
                totalEmployees,
                totalContracts,
                totalSalaries,
                activeContracts,
                pendingContracts,
                expiredContracts,
                expiringContracts,
                averageSalary
        );
    }

    public static List<DepartmentStats> getEmployeesPerDepartment() {
        List<DepartmentStats> list = new ArrayList<>();

        String sql = """
                SELECT d.name, COUNT(e.id) AS employee_count
                FROM departments d
                LEFT JOIN employees e ON e.department_id = d.id
                GROUP BY d.id, d.name
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                DepartmentStats stats = new DepartmentStats(
                        rs.getString("name"),
                        rs.getInt("employee_count")
                );

                list.add(stats);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static int getCount(String sql) {
        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static double getAverage(String sql) {
        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}