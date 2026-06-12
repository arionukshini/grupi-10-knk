package com.company.system.repository;

import com.company.system.model.Employee;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeRepository {

    List<Employee> findAll() throws SQLException;

    Employee findById(int employeeId) throws SQLException;

    List<Employee> findByDepartmentId(int departmentId, Integer excludeEmployeeId) throws SQLException;

    boolean save(Employee employee) throws SQLException;

    boolean update(Employee employee) throws SQLException;

    boolean deleteById(int employeeId) throws SQLException;
}
