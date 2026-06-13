package com.company.system.service;

import com.company.system.exceptions.DatabaseOperationException;
import com.company.system.model.Department;
import com.company.system.repository.DepartmentRepository;
import com.company.system.repository.jdbc.JdbcDepartmentRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentService {

    private static final DepartmentRepository departmentRepository = new JdbcDepartmentRepository();

    public static List<Department> getAllDepartments() {
        try {
            return departmentRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean addDepartment(Department department) {
        try {
            boolean saved = departmentRepository.save(department);
            if (!saved) {
                throw new DatabaseOperationException("exception.department.update.notFound");
            }
            return true;
        } catch (SQLException e) {
            throw new DatabaseOperationException("exception.department.update.database", e);
        }
    }

    public static boolean updateDepartment(Department department) {
        try {
            return departmentRepository.update(department);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteDepartment(int departmentId) {
        try {
            return departmentRepository.deleteById(departmentId);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public static Department getDepartmentById(int departmentId) {
        try {
            return departmentRepository.findById(departmentId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
