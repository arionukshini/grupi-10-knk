package com.company.system.repository.jdbc;

import com.company.system.db.DBConnection;
import com.company.system.mapper.DepartmentMapper;
import com.company.system.model.Department;
import com.company.system.repository.DepartmentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcDepartmentRepository implements DepartmentRepository {

    private final DepartmentMapper mapper = new DepartmentMapper();

    @Override
    public List<Department> findAll() throws SQLException {
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
                departments.add(mapper.fromResultSet(rs));
            }
        }

        return departments;
    }

    @Override
    public Department findById(int departmentId) throws SQLException {
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
                    return mapper.fromResultSet(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean save(Department department) throws SQLException {
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
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Department department) throws SQLException {
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
        }
    }

    @Override
    public boolean deleteById(int departmentId) throws SQLException {
        String sql = "DELETE FROM departments WHERE id = ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, departmentId);
            return stmt.executeUpdate() > 0;
        }
    }
}
