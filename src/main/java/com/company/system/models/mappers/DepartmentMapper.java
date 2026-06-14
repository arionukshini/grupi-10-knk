package com.company.system.models.mappers;

import com.company.system.models.Department;
import com.company.system.models.dto.DepartmentResponseDto;

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

    @Override
    public DepartmentResponseDto toDto(Department department) {
        return new DepartmentResponseDto(
                department.getId(),
                department.getName(),
                department.getDescription()
        );
    }
}
