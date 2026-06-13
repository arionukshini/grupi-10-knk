package com.company.system.service;

import com.company.system.model.dto.FullUserCreateRequestDto;
import com.company.system.model.dto.UserUpdateRequestDto;
import com.company.system.repository.UserProvisioningRepository;
import com.company.system.repository.UserRepository;
import com.company.system.repository.jdbc.JdbcUserProvisioningRepository;
import com.company.system.repository.jdbc.JdbcUserRepository;
import com.company.system.utils.PasswordUtils;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDate;

public class WizardUserService {

    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final UserRepository userRepository = new JdbcUserRepository();
    private static final UserProvisioningRepository userProvisioningRepository = new JdbcUserProvisioningRepository();

    public static boolean createFullUser(
            String firstName, String lastName, String email, String phone,
            String position, int departmentId, String empStatus,
            String contractType, LocalDate startDate, LocalDate endDate, String contractStatus,
            double gross, double bonus, double deductions, int workHours,
            int vacationDays, double overtimeHours,
            String username, String tempPassword
    ) {
        FullUserCreateRequestDto request = new FullUserCreateRequestDto(
                firstName,
                lastName,
                email,
                phone,
                position,
                departmentId,
                empStatus,
                contractType,
                startDate,
                endDate,
                contractStatus,
                gross,
                bonus,
                deductions,
                workHours,
                vacationDays,
                overtimeHours,
                username,
                tempPassword
        );

        try {
            return userProvisioningRepository.createFullUser(request, PasswordUtils.hashPassword(tempPassword));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String generateUniqueUsername(String base) {
        if (!usernameExists(base)) {
            return base;
        }

        int suffix = 2;
        while (true) {
            String candidate = base + suffix;
            if (!usernameExists(candidate)) {
                return candidate;
            }
            suffix++;
        }
    }

    public static String generateTempPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder value = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            value.append(TEMP_PASSWORD_CHARS.charAt(random.nextInt(TEMP_PASSWORD_CHARS.length())));
        }
        return value.toString();
    }

    public static boolean updateUser(int userId, Integer employeeId, String username, String newPassword) {
        String passwordHash = newPassword == null || newPassword.isBlank()
                ? null
                : PasswordUtils.hashPassword(newPassword);
        UserUpdateRequestDto request = new UserUpdateRequestDto(userId, employeeId, username, passwordHash);

        try {
            return userRepository.update(request);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEmployeeIdUnique(int employeeId, int excludeUserId) {
        try {
            return userRepository.isEmployeeIdUnique(employeeId, excludeUserId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean usernameExists(String username) {
        try {
            return userRepository.existsByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
