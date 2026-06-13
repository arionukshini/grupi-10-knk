package com.company.system.service;

import com.company.system.model.User;
import com.company.system.model.dto.LoginRequestDto;
import com.company.system.model.dto.PasswordResetRequestDto;
import com.company.system.model.dto.UserRegistrationRequestDto;
import com.company.system.repository.UserRepository;
import com.company.system.repository.jdbc.JdbcUserRepository;
import com.company.system.utils.PasswordUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static final UserRepository userRepository = new JdbcUserRepository();

    public static User login(String username, String password) {
        LoginRequestDto request = new LoginRequestDto(username, password);

        if (isBlank(request.username()) || isBlank(request.password())) {
            return null;
        }

        try {
            User user = userRepository.findByUsername(request.username());
            if (user != null && PasswordUtils.verifyPassword(request.password(), user.getPasswordHash())) {
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean register(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return false;
        }

        try {
            if (userRepository.existsByUsername(username)) {
                return false;
            }

            UserRegistrationRequestDto request = new UserRegistrationRequestDto(
                    username,
                    PasswordUtils.hashPassword(password),
                    "USER"
            );
            return userRepository.save(request);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static String getDisplayName(User user) {
        if (user == null) {
            return "-";
        }

        if (!isBlank(user.getEmployeeName())) {
            return user.getEmployeeName();
        }

        if (user.getEmployeeId() == null) {
            return user.getUsername();
        }

        try {
            String employeeName = userRepository.findEmployeeNameById(user.getEmployeeId());
            if (!isBlank(employeeName)) {
                return employeeName;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user.getUsername();
    }

    public static boolean resetPassword(String username, String newPassword) {
        if (isBlank(username) || isBlank(newPassword)) {
            return false;
        }

        PasswordResetRequestDto request = new PasswordResetRequestDto(
                username,
                PasswordUtils.hashPassword(newPassword)
        );

        try {
            return userRepository.updatePasswordByUsername(request);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getPasswordHashByUsername(String username) {
        if (isBlank(username)) {
            return null;
        }

        try {
            return userRepository.findPasswordHashByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean userExists(String username) {
        if (isBlank(username)) {
            return false;
        }

        try {
            return userRepository.existsByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changePasswordAndClearRequiredFlag(int userId, String newPassword) {
        if (userId <= 0 || isBlank(newPassword)) {
            return false;
        }

        try {
            return userRepository.updatePasswordByUserId(userId, PasswordUtils.hashPassword(newPassword));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getPasswordHashByUsernameAndEmail(String username, String email) {
        if (isBlank(username) || isBlank(email)) {
            return null;
        }

        try {
            return userRepository.findPasswordHashByUsernameAndEmail(username, email);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deleteUser(int userId) {
        try {
            return userRepository.deleteById(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isOnlyAdmin(User user) {
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return false;
        }

        try {
            return userRepository.countAdmins() <= 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean deleteAccountAndEmployeeData(User user) {
        if (user == null) {
            return false;
        }

        try {
            return userRepository.deleteAccountAndEmployeeData(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getEmployeeIdByUserId(int userId) {
        try {
            return userRepository.findEmployeeIdByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
