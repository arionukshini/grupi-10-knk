package com.company.system.models.mappers;

import com.company.system.models.User;
import com.company.system.models.dto.UserResponseDto;

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

    @Override
    public UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmployeeId(),
                user.getEmployeeName(),
                user.getRole(),
                user.getCreatedAt(),
                user.mustChangePassword()
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
