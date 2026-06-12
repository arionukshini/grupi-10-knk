package com.company.system.mapper;

import com.company.system.model.DashboardStats;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardStatsMapper implements Mapper<DashboardStats> {

    @Override
    public DashboardStats fromResultSet(ResultSet rs) throws SQLException {
        return new DashboardStats(
                rs.getInt("total_employees"),
                rs.getInt("total_contracts"),
                rs.getInt("total_salaries"),
                rs.getInt("total_departments"),
                rs.getInt("active_contracts"),
                rs.getInt("pending_contracts"),
                rs.getInt("expired_contracts"),
                rs.getInt("expiring_contracts"),
                rs.getDouble("average_salary")
        );
    }
}
