package com.company.system.mapper;

import com.company.system.model.SalaryByDepartmentStats;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SalaryByDepartmentStatsMapper implements Mapper<SalaryByDepartmentStats> {

    @Override
    public SalaryByDepartmentStats fromResultSet(ResultSet rs) throws SQLException {
        return new SalaryByDepartmentStats(
                rs.getString("name"),
                rs.getDouble("average_salary")
        );
    }
}
