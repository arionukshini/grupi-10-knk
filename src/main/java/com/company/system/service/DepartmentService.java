package com.company.system.service;


import com.company.system.exceptions.DatabaseOperationException;
import com.company.system.models.Department;
import com.company.system.repository.DepartmentRepository;
import com.company.system.utils.AppLogger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.company.system.utils.Validator.departmentNameValidator;
import static com.company.system.utils.Validator.descriptionValidator;

public class DepartmentService {

    private static final DepartmentRepository departmentRepository = new DepartmentRepository();

    public static List<Department> getAllDepartments() {
        try {
            return departmentRepository.findAll();
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return new ArrayList<>();
        }
    }

    public static boolean addDepartment(Department department) {
        if (!isValidDepartment(department)) {
            return false;
        }

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
        if (!isValidDepartment(department)) {
            return false;
        }

        try {
            return departmentRepository.update(department);
        } catch (SQLException e) {
            AppLogger.error("Failed to update department", e);
            return false;
        }
    }

    public static boolean deleteDepartment(int departmentId) {
        try {
            return departmentRepository.deleteById(departmentId);
        } catch (SQLException e) {
            AppLogger.error("Failed to delete department", e);
            return false;
        }
    }

    public static Department getDepartmentById(int departmentId) {
        try {
            return departmentRepository.findById(departmentId);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return null;
        }
    }

    private static boolean isValidDepartment(Department department) {
        if (department == null
                || !departmentNameValidator(department.getName())
                || !descriptionValidator(department.getDescription())) {
            AppLogger.info("Invalid department data rejected.");
            return false;
        }
        return true;
    }
}
