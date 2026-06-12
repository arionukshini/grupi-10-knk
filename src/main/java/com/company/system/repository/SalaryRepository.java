package com.company.system.repository;

import com.company.system.model.Salary;

import java.sql.SQLException;
import java.util.List;

public interface SalaryRepository {

    List<Salary> findAll() throws SQLException;

    Salary findLatestByEmployeeId(int employeeId) throws SQLException;

    List<Salary> findHistoryByEmployeeId(int employeeId) throws SQLException;

    boolean save(Salary salary) throws SQLException;

    boolean update(Salary salary) throws SQLException;

    boolean deleteById(int salaryId) throws SQLException;

    boolean saveHistory(Salary salary) throws SQLException;
}
