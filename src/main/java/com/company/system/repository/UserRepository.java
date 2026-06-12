package com.company.system.repository;

import com.company.system.model.User;
import com.company.system.model.dto.PasswordResetRequestDto;
import com.company.system.model.dto.UserRegistrationRequestDto;
import com.company.system.model.dto.UserUpdateRequestDto;

import java.sql.SQLException;
import java.util.List;

public interface UserRepository {

    User findByUsername(String username) throws SQLException;

    List<User> findAll() throws SQLException;

    boolean existsByUsername(String username) throws SQLException;

    boolean save(UserRegistrationRequestDto request) throws SQLException;

    boolean update(UserUpdateRequestDto request) throws SQLException;

    boolean updatePasswordByUsername(PasswordResetRequestDto request) throws SQLException;

    boolean updatePasswordByUserId(int userId, String passwordHash) throws SQLException;

    String findPasswordHashByUsername(String username) throws SQLException;

    String findPasswordHashByUsernameAndEmail(String username, String email) throws SQLException;

    String findEmployeeNameById(Integer employeeId) throws SQLException;

    int findEmployeeIdByUserId(int userId) throws SQLException;

    boolean deleteById(int userId) throws SQLException;

    boolean deleteAccountAndEmployeeData(User user) throws SQLException;

    int countAdmins() throws SQLException;

    boolean isEmployeeIdUnique(int employeeId, int excludeUserId) throws SQLException;
}
