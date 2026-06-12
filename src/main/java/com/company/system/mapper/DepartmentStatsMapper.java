package com.company.system.mapper;

import com.company.system.model.DepartmentStats;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartmentStatsMapper implements Mapper<DepartmentStats> {

    @Override
    public DepartmentStats fromResultSet(ResultSet rs) throws SQLException {
        return new DepartmentStats(
                rs.getString("name"),
                rs.getInt("employee_count")
        );
    }
}
