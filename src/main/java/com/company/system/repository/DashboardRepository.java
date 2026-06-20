package com.company.system.repository;

import com.company.system.db.DBConnection;
import com.company.system.models.mappers.DashboardStatsMapper;
import com.company.system.models.mappers.DepartmentStatsMapper;
import com.company.system.models.mappers.SalaryByDepartmentStatsMapper;
import com.company.system.models.DashboardStats;
import com.company.system.models.DepartmentStats;
import com.company.system.models.SalaryByDepartmentStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardRepository extends BaseRepository<DashboardStats> {

    private final DashboardStatsMapper dashboardStatsMapper = new DashboardStatsMapper();
    private final DepartmentStatsMapper departmentStatsMapper = new DepartmentStatsMapper();
    private final SalaryByDepartmentStatsMapper salaryByDepartmentStatsMapper = new SalaryByDepartmentStatsMapper();
    public DashboardStats findDashboardStats() throws SQLException {
        String sql = """
                SELECT
                    (SELECT COUNT(*) FROM employees) AS total_employees,
                    (SELECT COUNT(*) FROM contracts) AS total_contracts,
                    (SELECT COUNT(*) FROM salaries) AS total_salaries,
                    (SELECT COUNT(*) FROM departments) AS total_departments,
                    (SELECT COUNT(*) FROM contracts WHERE status = 'Active') AS active_contracts,
                    (SELECT COUNT(*) FROM contracts WHERE status = 'Pending') AS pending_contracts,
                    (SELECT COUNT(*) FROM contracts WHERE status = 'Expired') AS expired_contracts,
                    (SELECT COUNT(*) FROM contracts
                     WHERE end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)) AS expiring_contracts,
                    COALESCE((SELECT AVG(net_salary) FROM salaries), 0) AS average_salary
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return dashboardStatsMapper.fromResultSet(rs);
            }
        }

        return new DashboardStats(0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
    public List<DepartmentStats> findEmployeesPerDepartment() throws SQLException {
        List<DepartmentStats> stats = new ArrayList<>();
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
                stats.add(departmentStatsMapper.fromResultSet(rs));
            }
        }

        return stats;
    }
    public Map<String, Integer> findContractsByStatus() throws SQLException {
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
        }

        return statusCounts;
    }
    public List<SalaryByDepartmentStats> findAverageSalaryPerDepartment() throws SQLException {
        List<SalaryByDepartmentStats> stats = new ArrayList<>();
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
                stats.add(salaryByDepartmentStatsMapper.fromResultSet(rs));
            }
        }

        return stats;
    }
}
