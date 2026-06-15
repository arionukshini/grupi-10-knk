package com.company.system.db;

import com.company.system.utils.AppLogger;
import com.company.system.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class DemoDataSeeder {

    private DemoDataSeeder() {
    }

    static void seed(Connection connection) throws SQLException {
        seedDemoCompanyData(connection);
        normalizeDemoNames(connection);
        seedDefaultAdmin(connection);
        seedEmployeeUsers(connection);
    }

    private static void seedDemoCompanyData(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM departments")) {
            resultSet.next();

            if (resultSet.getInt(1) > 0) {
                return;
            }

            statement.executeUpdate("""
                    INSERT INTO departments (name, description) VALUES
                    ('Human Resources', 'Handles employee management and recruitment'),
                    ('Finance', 'Manages company finances and payroll'),
                    ('IT', 'Maintains systems and technical infrastructure'),
                    ('Marketing', 'Handles advertising and promotions'),
                    ('Sales', 'Responsible for product and service sales'),
                    ('Operations', 'Oversees daily business operations and workflows'),
                    ('Customer Support', 'Provides assistance and support to customers');
                    """);

            statement.executeUpdate("""
                    INSERT INTO employees
                    (first_name, last_name, email, phone, position, department_id, hire_date, base_salary, status)
                    VALUES
                    ('Arion', 'Ukshini', 'arion.ukshini@company.com', '+38344111222', 'Software Engineer', 3, '2026-01-15', 1200.00, 'Active'),
                    ('Arjanita', 'Lestrani', 'arjanita.lestrani@company.com', '+38344111333', 'HR Manager', 1, '2025-11-10', 1100.00, 'Active'),
                    ('Florentina', 'Dervishaj', 'florentina.dervishaj@company.com', '+38344111444', 'Accountant', 2, '2025-09-01', 1000.00, 'Active'),
                    ('Edison', 'Ukshini', 'edison.ukshini@company.com', '+38344111555', 'Marketing Specialist', 4, '2026-03-20', 950.00, 'Active'),
                    ('Arijola', 'Krasniqi', 'arijola.krasniqi@company.com', '+38344111666', 'Sales Representative', 5, '2025-11-05', 900.00, 'Inactive'),
                    ('Alketa', 'Thaqi', 'alketa.thaqi@company.com', '+38344111777', 'System Administrator', 3, '2025-12-12', 1300.00, 'Active');
                    """);

            statement.executeUpdate("""
                    INSERT INTO contracts
                    (employee_id, contract_type, start_date, end_date, salary, status)
                    VALUES
                    (1, 'Full-Time', '2026-01-15', '2028-01-15', 1200.00, 'Active'),
                    (2, 'Full-Time', '2025-11-10', '2027-11-10', 1100.00, 'Active'),
                    (3, 'Full-Time', '2025-09-01', '2027-09-01', 1000.00, 'Active'),
                    (4, 'Part-Time', '2026-03-20', '2027-03-20', 950.00, 'Active'),
                    (5, 'Internship', '2025-11-05', '2026-05-15', 900.00, 'Expired'),
                    (6, 'Full-Time', '2025-12-12', '2027-12-12', 1300.00, 'Active');
                    """);

            statement.executeUpdate("""
                    INSERT INTO salaries
                    (employee_id,gross_salary,bonus,deductions,worked_days,vacation_days,work_hours,overtime_hours,net_salary,payment_date)
                    VALUES
                    (1, 1200.00, 100.00, 50.00, 20, 2, 160, 10, 1250.00, '2026-05-31'),
                    (2, 1100.00, 50.00, 20.00, 21, 1, 155, 5, 1130.00, '2026-05-31'),
                    (3, 1000.00, 0.00, 30.00, 22, 0, 150, 0, 970.00, '2026-05-31'),
                    (4, 950.00, 25.00, 15.00, 19, 3, 145, 2, 960.00, '2026-05-31'),
                    (5, 900.00, 0.00, 10.00, 18, 0, 140, 0, 890.00, '2026-05-31'),
                    (6, 1300.00, 150.00, 60.00, 22, 1, 170, 12, 1390.00, '2026-05-31');
                    """);

            statement.executeUpdate("""
                    INSERT INTO salary_history
                    (salary_id,employee_id,gross_salary,bonus,deductions,net_salary,payment_date)
                    VALUES
                    (1, 1, 1200.00, 100.00, 50.00, 1250.00, '2026-05-31'),
                    (2, 2, 1100.00, 50.00, 20.00, 1130.00, '2026-05-31'),
                    (3, 3, 1000.00, 0.00, 30.00, 970.00, '2026-05-31'),
                    (4, 4, 950.00, 25.00, 15.00, 960.00, '2026-05-31'),
                    (5, 5, 900.00, 0.00, 10.00, 890.00, '2026-05-31'),
                    (6, 6, 1300.00, 150.00, 60.00, 1390.00, '2026-05-31');
                    """);

            statement.executeUpdate("""
                    INSERT INTO vacation_requests
                    (employee_id, request_type, start_date, end_date, reason, status)
                    VALUES
                    (1, 'Annual Leave', '2026-06-08', '2026-06-12', 'Family travel planned for the week.', 'Pending'),
                    (3, 'Medical Leave', '2026-06-02', '2026-06-04', 'Medical appointment and recovery days.', 'Pending'),
                    (5, 'Holiday', '2026-06-15', '2026-06-16', 'Personal holiday request.', 'Approved');
                    """);

            AppLogger.info("Demo data inserted.");
        }
    }

    private static void normalizeDemoNames(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    UPDATE employees
                    SET first_name = 'Alketa',
                        email = 'alketa.thaqi@company.com'
                    WHERE first_name = 'Alekta'
                      AND last_name = 'Thaqi'
                    """);

            statement.executeUpdate("""
                    UPDATE users
                    SET username = 'alketa.thaqi'
                    WHERE username = 'alekta.thaqi'
                    """);
        }
    }

    private static void seedDefaultAdmin(Connection connection) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM users";
        String insertSql = "INSERT INTO users (employee_id, username, password_hash, role, must_change_password) VALUES (NULL, ?, ?, ?, FALSE)";

        try (Statement countStatement = connection.createStatement();
             ResultSet resultSet = countStatement.executeQuery(countSql)) {
            resultSet.next();

            if (resultSet.getInt(1) > 0) {
                return;
            }
        }

        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setString(1, "admin");
            statement.setString(2, PasswordUtils.hashPassword("1234"));
            statement.setString(3, "ADMIN");
            statement.executeUpdate();
        }
    }

    private static void seedEmployeeUsers(Connection connection) throws SQLException {
        String[][] users = {
                {"Arion", "Ukshini", "arion.ukshini"},
                {"Arjanita", "Lestrani", "arjanita.lestrani"},
                {"Florentina", "Dervishaj", "florentina.dervishaj"},
                {"Edison", "Ukshini", "edison.ukshini"},
                {"Arijola", "Krasniqi", "arijola.krasniqi"},
                {"Alketa", "Thaqi", "alketa.thaqi"}
        };

        String findEmployeeSql = "SELECT id FROM employees WHERE first_name = ? AND last_name = ?";
        String userExistsSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (employee_id, username, password_hash, role, must_change_password) VALUES (?, ?, ?, 'USER', TRUE)";

        try (PreparedStatement findEmployeeStatement = connection.prepareStatement(findEmployeeSql);
             PreparedStatement userExistsStatement = connection.prepareStatement(userExistsSql);
             PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {

            for (String[] user : users) {
                findEmployeeStatement.setString(1, user[0]);
                findEmployeeStatement.setString(2, user[1]);

                int employeeId;
                try (ResultSet employeeResultSet = findEmployeeStatement.executeQuery()) {
                    if (!employeeResultSet.next()) {
                        continue;
                    }

                    employeeId = employeeResultSet.getInt("id");
                }

                userExistsStatement.setString(1, user[2]);
                try (ResultSet userResultSet = userExistsStatement.executeQuery()) {
                    userResultSet.next();

                    if (userResultSet.getInt(1) > 0) {
                        continue;
                    }
                }

                insertStatement.setInt(1, employeeId);
                insertStatement.setString(2, user[2]);
                insertStatement.setString(3, PasswordUtils.hashPassword("1234"));
                insertStatement.executeUpdate();
            }
        }
    }
}
