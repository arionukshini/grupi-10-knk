package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.exceptions.DatabaseOperationException;
import com.company.system.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentService {

    public static List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();

        String sql = """
                SELECT id, name, description
                FROM departments
                ORDER BY id
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Department department = new Department(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );

                departments.add(department);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departments;
    }

    public static boolean addDepartment(Department department) {
        String sql = """
                INSERT INTO departments (name, description)
                VALUES (?, ?)
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, department.getName());
            stmt.setString(2, department.getDescription());

            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new DatabaseOperationException("exception.department.update.notFound");
            }

            return true;

        } catch (SQLException e) {
            throw new DatabaseOperationException("exception.department.update.database", e);
        }
    }

    public static boolean updateDepartment(Department department) {
        String sql = """
                UPDATE departments
                SET name = ?, description = ?
                WHERE id = ?
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, department.getName());
            stmt.setString(2, department.getDescription());
            stmt.setInt(3, department.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return false;
    }

    public static boolean deleteDepartment(int departmentId) {
        String sql = "DELETE FROM departments WHERE id = ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, departmentId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return false;
    }
    public static Department getDepartmentById(int departmentId) {
        String sql = """
                SELECT id, name, description
                FROM departments
                WHERE id = ?
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, departmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Department(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
