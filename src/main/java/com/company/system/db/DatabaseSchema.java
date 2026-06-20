package com.company.system.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class DatabaseSchema {

    private DatabaseSchema() {
    }

    static void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS departments (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL UNIQUE,
                        description VARCHAR(255)
                    );
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employees (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        first_name VARCHAR(50) NOT NULL,
                        last_name VARCHAR(50) NOT NULL,
                        email VARCHAR(100) UNIQUE,
                        phone VARCHAR(20),
                        position VARCHAR(100),
                        department_id INT,
                        hire_date DATE,
                        base_salary DECIMAL(10,2),
                        status ENUM('Active', 'Inactive', 'Suspended', 'Pending'),
                        FOREIGN KEY (department_id) REFERENCES departments(id)
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS contracts (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        employee_id INT NOT NULL,
                        contract_type ENUM('Full-Time', 'Part-Time', 'Temporary', 'Internship'),
                        start_date DATE,
                        end_date DATE,
                        salary DOUBLE,
                        status ENUM('Active', 'Expired', 'Pending'),
                        FOREIGN KEY (employee_id) REFERENCES employees(id)
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS salaries (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        employee_id INT NOT NULL,
                        gross_salary DECIMAL(10,2) NOT NULL,
                        bonus DECIMAL(10,2) DEFAULT 0,
                        deductions DECIMAL(10,2) DEFAULT 0,
                        vacation_days INT DEFAULT 0,
                        work_hours DECIMAL(10,2) DEFAULT 0,
                        overtime_hours DECIMAL(10,2) DEFAULT 0,
                        daily_rate DECIMAL(10,2) DEFAULT 0,
                        overtime_pay DECIMAL(10,2) DEFAULT 0,
                        net_salary DECIMAL(10,2) NOT NULL,
                        payment_date DATE NOT NULL,
                        FOREIGN KEY (employee_id) REFERENCES employees(id)
                    );
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS salary_history (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        salary_id INT,
                        employee_id INT NOT NULL,
                        gross_salary DECIMAL(10,2),
                        bonus DECIMAL(10,2),
                        deductions DECIMAL(10,2),
                        net_salary DECIMAL(10,2),
                        payment_date DATE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (salary_id) REFERENCES salaries(id),
                        FOREIGN KEY (employee_id) REFERENCES employees(id)
                    );
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS vacation_requests (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        employee_id INT NOT NULL,
                        request_type VARCHAR(60) NOT NULL DEFAULT 'Annual Leave',
                        start_date DATE NOT NULL,
                        end_date DATE NOT NULL,
                        reason VARCHAR(500),
                        status ENUM('Pending', 'Approved', 'Rejected') NOT NULL DEFAULT 'Pending',
                        admin_response VARCHAR(500),
                        requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        reviewed_at TIMESTAMP NULL,
                        FOREIGN KEY (employee_id) REFERENCES employees(id)
                    );
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(100) NOT NULL,
                        password_hash VARCHAR(255) NOT NULL,
                        role VARCHAR(20) DEFAULT 'USER',
                        employee_id INT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        must_change_password BOOLEAN NOT NULL DEFAULT FALSE
                    )
                    """);
        }
    }

    static void applyMigrations(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            addColumnIfMissing(statement, "salaries", "worked_days", "worked_days INT DEFAULT 0");
            addColumnIfMissing(statement, "users", "must_change_password",
                    "must_change_password BOOLEAN NOT NULL DEFAULT FALSE");
        }
    }

    private static void addColumnIfMissing(
            Statement statement,
            String table,
            String column,
            String columnDefinition
    ) throws SQLException {
        try (ResultSet columns = statement.executeQuery("SHOW COLUMNS FROM " + table + " LIKE '" + column + "'")) {
            if (!columns.next()) {
                statement.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + columnDefinition);
            }
        }
    }
}
