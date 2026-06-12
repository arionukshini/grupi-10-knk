package com.company.system.mapper;

import com.company.system.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements Mapper<User> {

    @Override
    public User fromResultSet(ResultSet rs) throws SQLException {
        Object employeeIdValue = rs.getObject("employee_id");
        Integer employeeId = employeeIdValue == null ? null : ((Number) employeeIdValue).intValue();

        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                employeeId,
                readOptionalString(rs, "employee_name"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getTimestamp("created_at"),
                rs.getBoolean("must_change_password")
        );
    }

    private String readOptionalString(ResultSet rs, String columnName) throws SQLException {
        try {
            return rs.getString(columnName);
        } catch (SQLException e) {
            return null;
        }
    }
}
