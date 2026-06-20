package com.company.system.utils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;

public class Validator {
    private static final Set<String> EMPLOYEE_STATUSES = Set.of(
            "Active",
            "Pending",
            "Inactive"
    );

    public static boolean emailValidator(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@company\\.com$");
    }

    public static boolean salaryValidator(double salary) {
        return salary >= 0;
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean hasMaxLength(String value, int maxLength) {
        return value == null || value.trim().length() <= maxLength;
    }

    public static boolean isPositive(int value) {
        return value > 0;
    }

    public static boolean phoneValidator(String phone) {
        return phone == null || phone.isBlank() || phone.matches("^\\+?[0-9][0-9\\s-]{5,19}$");
    }

    public static boolean departmentNameValidator(String name) {
        return isNotBlank(name) && hasMaxLength(name, 100);
    }

    public static boolean descriptionValidator(String description) {
        return hasMaxLength(description, 255);
    }

    public static boolean employeeNameValidator(String name) {
        return isNotBlank(name) && hasMaxLength(name, 50);
    }

    public static boolean positionValidator(String position) {
        return isNotBlank(position) && hasMaxLength(position, 100);
    }

    public static boolean hireDateValidator(Date hireDate) {
        return hireDate != null && !hireDate.toLocalDate().isAfter(LocalDate.now());
    }

    public static boolean statusValidator(String status) {
        return status != null && EMPLOYEE_STATUSES.contains(status);
    }
}
