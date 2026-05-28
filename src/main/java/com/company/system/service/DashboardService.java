package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.DashboardStats;
import com.company.system.model.DepartmentStats;
import com.company.system.model.SalaryByDepartmentStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {

    public static DashboardStats getDashboardStats() {
        int totalEmployees = getCount("SELECT COUNT(*) FROM employees");
        int totalContracts = getCount("SELECT COUNT(*) FROM contracts");
        int totalSalaries = getCount("SELECT COUNT(*) FROM salaries");
        int totalDepartments = getCount("SELECT COUNT(*) FROM departments");

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
                totalDepartments,
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

    public static Map<String, Integer> getContractsByStatus() {
        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        statusCounts.put("Active", 0);
        statusCounts.put("Pending", 0);
        statusCounts.put("Expired", 0);

        String sql = """
                SELECT status, COUNT(*) AS contract_count
                FROM contracts
                GROUP BY status
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                statusCounts.put(rs.getString("status"), rs.getInt("contract_count"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusCounts;
    }

    public static List<SalaryByDepartmentStats> getAverageSalaryPerDepartment() {
        List<SalaryByDepartmentStats> list = new ArrayList<>();

        String sql = """
                SELECT d.name, AVG(s.net_salary) AS average_salary
                FROM departments d
                JOIN employees e ON e.department_id = d.id
                JOIN salaries s ON s.employee_id = e.id
                GROUP BY d.id, d.name
                ORDER BY average_salary DESC
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                list.add(new SalaryByDepartmentStats(
                        rs.getString("name"),
                        rs.getDouble("average_salary")
                ));
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
