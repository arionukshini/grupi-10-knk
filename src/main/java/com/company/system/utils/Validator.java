package com.company.system.utils;

public class Validator {
    public static boolean emailValidator(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@company\\.com$");
    }

    public static boolean salaryValidator(double salary) {
        return salary >= 0;
    }
}