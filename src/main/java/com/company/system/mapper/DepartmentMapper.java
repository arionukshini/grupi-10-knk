package com.company.system.mapper;

import com.company.system.model.Department;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartmentMapper implements Mapper<Department> {

    @Override
    public Department fromResultSet(ResultSet rs) throws SQLException {
        return new Department(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }
}
