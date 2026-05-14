package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentService {

    public static List<Department> getAllDepartments() {

        List<Department> departments = new ArrayList<>();

        String sql = "SELECT * FROM departments";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                Department department = new Department(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location")
                );

                departments.add(department);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departments;
    }

    public static void addDepartment(Department department) {

        String sql = """
                INSERT INTO departments
                (
                    name,
                    location
                )
                VALUES (?, ?)
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, department.getName());
            stmt.setString(2, department.getLocation());

            stmt.executeUpdate();

            System.out.println("Department added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}