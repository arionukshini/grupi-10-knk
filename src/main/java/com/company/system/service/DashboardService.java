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

        int totalEmployees = 0;
        int totalDepartments = 0;
        int activeContracts = 0;
        double averageSalary = 0;

        try (
                Connection conn = DBConnection.connect()
        ) {

            String employeesQuery =
                    "SELECT COUNT(*) AS total FROM employees";

            PreparedStatement empStmt =
                    conn.prepareStatement(employeesQuery);

            ResultSet empRs = empStmt.executeQuery();

            if (empRs.next()) {
                totalEmployees = empRs.getInt("total");
            }

            String departmentsQuery =
                    "SELECT COUNT(*) AS total FROM departments";

            PreparedStatement depStmt =
                    conn.prepareStatement(departmentsQuery);

            ResultSet depRs = depStmt.executeQuery();

            if (depRs.next()) {
                totalDepartments = depRs.getInt("total");
            }

            String contractsQuery =
                    """
                    SELECT COUNT(*) AS total
                    FROM contracts
                    WHERE status = 'Active'
                    """;

            PreparedStatement conStmt =
                    conn.prepareStatement(contractsQuery);

            ResultSet conRs = conStmt.executeQuery();

            if (conRs.next()) {
                activeContracts = conRs.getInt("total");
            }

            String salaryQuery =
                    "SELECT AVG(base_salary) AS avg_salary FROM employees";

            PreparedStatement salStmt =
                    conn.prepareStatement(salaryQuery);

            ResultSet salRs = salStmt.executeQuery();

            if (salRs.next()) {
                averageSalary = salRs.getDouble("avg_salary");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DashboardStats(
                totalEmployees,
                totalDepartments,
                activeContracts,
                averageSalary
        );
    }

    public static List<DepartmentStats> getEmployeesPerDepartment() {

        List<DepartmentStats> stats = new ArrayList<>();

        String query =
                """
                SELECT departments.name,
                       COUNT(employees.id) AS employee_count
                FROM departments
                LEFT JOIN employees
                ON departments.id = employees.department_id
                GROUP BY departments.name
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt =
                        conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                DepartmentStats departmentStats =
                        new DepartmentStats(
                                rs.getString("name"),
                                rs.getInt("employee_count")
                        );

                stats.add(departmentStats);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }
}