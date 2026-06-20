package com.company.system.service;


import com.company.system.utils.AppLogger;
import com.company.system.models.DashboardStats;
import com.company.system.models.DepartmentStats;
import com.company.system.models.SalaryByDepartmentStats;
import com.company.system.repository.DashboardRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {

    private static final DashboardRepository dashboardRepository = new DashboardRepository();

    public static DashboardStats getDashboardStats() {
        try {
            return dashboardRepository.findDashboardStats();
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return new DashboardStats(0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    public static List<DepartmentStats> getEmployeesPerDepartment() {
        try {
            return dashboardRepository.findEmployeesPerDepartment();
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return new ArrayList<>();
        }
    }

    public static Map<String, Integer> getContractsByStatus() {
        try {
            return dashboardRepository.findContractsByStatus();
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            Map<String, Integer> statusCounts = new LinkedHashMap<>();
            statusCounts.put("Active", 0);
            statusCounts.put("Pending", 0);
            statusCounts.put("Expired", 0);
            return statusCounts;
        }
    }

    public static List<SalaryByDepartmentStats> getAverageSalaryPerDepartment() {
        try {
            return dashboardRepository.findAverageSalaryPerDepartment();
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return new ArrayList<>();
        }
    }
}
