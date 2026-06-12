package com.company.system.repository;

import com.company.system.model.Department;

import java.sql.SQLException;
import java.util.List;

public interface DepartmentRepository {

    List<Department> findAll() throws SQLException;

    Department findById(int departmentId) throws SQLException;

    boolean save(Department department) throws SQLException;

    boolean update(Department department) throws SQLException;

    boolean deleteById(int departmentId) throws SQLException;
}
