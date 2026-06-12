package com.company.system.mapper;

import com.company.system.model.Salary;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SalaryHistoryMapper implements Mapper<Salary> {

    @Override
    public Salary fromResultSet(ResultSet rs) throws SQLException {
        return new Salary(
                0,
                rs.getInt("employee_id"),
                rs.getDouble("gross_salary"),
                rs.getDouble("bonus"),
                rs.getDouble("deductions"),
                0,
                0,
                0,
                0,
                0,
                0,
                rs.getDouble("net_salary"),
                rs.getDate("payment_date")
        );
    }
}
