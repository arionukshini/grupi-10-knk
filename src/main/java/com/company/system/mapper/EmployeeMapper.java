package com.company.system.mapper;

import com.company.system.model.Employee;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeMapper implements Mapper<Employee> {

    @Override
    public Employee fromResultSet(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("position"),
                rs.getInt("department_id"),
                rs.getDate("hire_date"),
                rs.getDouble("base_salary"),
                rs.getString("status")
        );
    }
}
