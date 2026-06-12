package com.company.system.repository;

import com.company.system.model.DashboardStats;
import com.company.system.model.DepartmentStats;
import com.company.system.model.SalaryByDepartmentStats;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DashboardRepository {

    DashboardStats findDashboardStats() throws SQLException;

    List<DepartmentStats> findEmployeesPerDepartment() throws SQLException;

    Map<String, Integer> findContractsByStatus() throws SQLException;

    List<SalaryByDepartmentStats> findAverageSalaryPerDepartment() throws SQLException;
}
