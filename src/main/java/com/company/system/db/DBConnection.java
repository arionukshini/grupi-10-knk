package com.company.system.db;

import com.company.system.utils.PasswordUtils;

import java.sql.*;

public class DBConnection {

    private static final String ROOT_URL =
            "jdbc:mysql://localhost:3306/";

    private static final String URL =
            "jdbc:mysql://localhost:3306/grupi_10";

    private static final String USER = "root";
    private static final String PASSWORD = "pass";

    // =========================
    // CONNECT TO DATABASE
    // =========================

    public static Connection connect() {

        try {

            return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

        } catch (SQLException e) {

            System.out.println("Connection failed!");
            e.printStackTrace();

            return null;
        }
    }

    // =========================
    // INITIALIZE DATABASE
    // =========================

    public static void initializeDatabase() {

        try {

            Connection rootConnection =
                    DriverManager.getConnection(
                            ROOT_URL,
                            USER,
                            PASSWORD
                    );

            Statement rootStatement =
                    rootConnection.createStatement();

            rootStatement.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS grupi_10"
            );

            rootConnection.close();

            Connection conn = connect();

            Statement stmt = conn.createStatement();


            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS departments (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                    
                        name VARCHAR(100) NOT NULL UNIQUE,
                        description VARCHAR(255)
                    );
                    """);

            stmt.executeUpdate("""
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
                    
                        FOREIGN KEY (department_id)
                        REFERENCES departments(id)
                    )
                    """);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS contracts (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                    
                        employee_id INT NOT NULL,
                    
                        contract_type ENUM('Full-Time', 'Part-Time', 'Temporary', 'Internship'),
                    
                        start_date DATE,
                        end_date DATE,
                    
                        salary DOUBLE,
                    
                        status ENUM('Active', 'Expired', 'Pending'),
                    
                        FOREIGN KEY (employee_id)
                        REFERENCES employees(id)
                    )
                    """);

            stmt.executeUpdate("""
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

            stmt.executeUpdate("""
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

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(100) NOT NULL,
                        password_hash VARCHAR(255) NOT NULL,
                        role VARCHAR(20) DEFAULT 'USER',
                        employee_id INT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        must_reset_password BOOLEAN NOT NULL DEFAULT FALSE
                    )
                    """);

            ResultSet rs =
                    stmt.executeQuery(
                            "SELECT COUNT(*) FROM departments"
                    );

            rs.next();

            int count = rs.getInt(1);

            // insert te dhena demo per testim

            if (count == 0) {

                stmt.executeUpdate("""
                        INSERT INTO departments (name, description) VALUES
                        ('Human Resources', 'Handles employee management and recruitment'),
                        ('Finance', 'Manages company finances and payroll'),
                        ('IT', 'Maintains systems and technical infrastructure'),
                        ('Marketing', 'Handles advertising and promotions'),
                        ('Sales', 'Responsible for product and service sales'),
                        ('Operations', 'Oversees daily business operations and workflows'),
                        ('Customer Support', 'Provides assistance and support to customers');
                        """);

                stmt.executeUpdate("""
                        INSERT INTO employees\s
                         (first_name, last_name, email, phone, position, department_id, hire_date, base_salary, status)
                         VALUES
                         ('Arion', 'Ukshini', 'arion.ukshini@company.com', '+38344111222', 'Software Engineer', 3, '2024-01-15', 1200.00, 'Active'),
                        
                         ('Arjanita', 'Lestrani', 'arjanita.lestrani@company.com', '+38344111333', 'HR Manager', 1, '2023-06-10', 1100.00, 'Active'),
                        
                         ('Florentina', 'Dervishaj', 'florentina.dervishaj@company.com', '+38344111444', 'Accountant', 2, '2022-09-01', 1000.00, 'Active'),
                        
                         ('Edison', 'Ukshini', 'edison.ukshini@company.com', '+38344111555', 'Marketing Specialist', 4, '2024-03-20', 950.00, 'Active'),
                        
                         ('Arijola', 'Krasniqi', 'arijola.krasniqi@company.com', '+38344111666', 'Sales Representative', 5, '2023-11-05', 900.00, 'Inactive'),
                        
                         ('Alekta', 'Thaqi', 'alekta.thaqi@company.com', '+38344111777', 'System Administrator', 3, '2021-12-12', 1300.00, 'Active');
                        """);

                stmt.executeUpdate("""
                        INSERT INTO contracts
                         (employee_id, contract_type, start_date, end_date, salary, status)
                         VALUES
                         (1, 'Full-Time', '2024-01-15', '2026-01-15', 1200.00, 'Active'),
                        
                         (2, 'Full-Time', '2023-06-10', '2025-06-10', 1100.00, 'Active'),
                        
                         (3, 'Full-Time', '2022-09-01', '2025-09-01', 1000.00, 'Active'),
                        
                         (4, 'Part-Time', '2024-03-20', '2025-03-20', 950.00, 'Active'),
                        
                         (5, 'Internship', '2023-11-05', '2024-11-05', 900.00, 'Expired'),
                        
                         (6, 'Full-Time', '2021-12-12', '2026-12-12', 1300.00, 'Active');
                        """);

                stmt.executeUpdate("""
                        INSERT INTO salaries
                        (employee_id,gross_salary,bonus,deductions,vacation_days,work_hours,overtime_hours,net_salary,payment_date)
                        VALUES
                        (1, 1200.00, 100.00, 50.00, 2, 160, 10, 1250.00, '2026-05-01'),
                        
                        (2, 1100.00, 50.00, 20.00, 1, 155, 5, 1130.00, '2026-05-01'),
                        
                        (3, 1000.00, 0.00, 30.00, 0, 150, 0, 970.00, '2026-05-01'),
                        
                        (4, 950.00, 25.00, 15.00, 3, 145, 2, 960.00, '2026-05-01'),
                        
                        (5, 900.00, 0.00, 10.00, 0, 140, 0, 890.00, '2026-05-01'),
                        
                        (6, 1300.00, 150.00, 60.00, 1, 170, 12, 1390.00, '2026-05-01');
                        """);

                stmt.executeUpdate("""
                            INSERT INTO salary_history
                            (salary_id,employee_id,gross_salary,bonus,deductions,net_salary,payment_date)
                            VALUES
                            (1, 1, 1200.00, 100.00, 50.00, 1250.00, '2026-05-01'),
                        
                            (2, 2, 1100.00, 50.00, 20.00, 1130.00, '2026-05-01'),
                        
                            (3, 3, 1000.00, 0.00, 30.00, 970.00, '2026-05-01'),
                        
                            (4, 4, 950.00, 25.00, 15.00, 960.00, '2026-05-01'),
                        
                            (5, 5, 900.00, 0.00, 10.00, 890.00, '2026-05-01'),
                        
                            (6, 6, 1300.00, 150.00, 60.00, 1390.00, '2026-05-01');
                        """);

                System.out.println("Demo data inserted!");
            }

            seedDefaultAdmin(conn);
            seedEmployeeUsers(conn);
            conn.close();

            System.out.println(
                    "Database initialized successfully!"
            );

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private static void seedDefaultAdmin(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM users";
        String insertSql = "INSERT INTO users (employee_id, username, password_hash, role, must_reset_password) VALUES (NULL, ?, ?, ?, FALSE)";

        try (Statement countStmt = conn.createStatement();
             ResultSet rs = countStmt.executeQuery(countSql)) {

            rs.next();

            if (rs.getInt(1) > 0) {
                return;
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, "admin");
            stmt.setString(2, PasswordUtils.hashPassword("1234"));
            stmt.setString(3, "ADMIN");
            stmt.executeUpdate();
        }
    }

    private static void seedEmployeeUsers(Connection conn) throws SQLException {
        String[][] users = {
                {"Arion", "Ukshini", "arion.ukshini"},
                {"Arjanita", "Lestrani", "arjanita.lestrani"},
                {"Florentina", "Dervishaj", "florentina.dervishaj"},
                {"Edison", "Ukshini", "edison.ukshini"},
                {"Arijola", "Krasniqi", "arijola.krasniqi"},
                {"Alekta", "Thaqi", "alekta.thaqi"}
        };

        String findEmployeeSql = "SELECT id FROM employees WHERE first_name = ? AND last_name = ?";
        String userExistsSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (employee_id, username, password_hash, role, must_reset_password) VALUES (?, ?, ?, 'USER', TRUE)";

        try (PreparedStatement findEmployeeStmt = conn.prepareStatement(findEmployeeSql);
             PreparedStatement userExistsStmt = conn.prepareStatement(userExistsSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            for (String[] user : users) {
                findEmployeeStmt.setString(1, user[0]);
                findEmployeeStmt.setString(2, user[1]);

                int employeeId;
                try (ResultSet employeeRs = findEmployeeStmt.executeQuery()) {
                    if (!employeeRs.next()) {
                        continue;
                    }

                    employeeId = employeeRs.getInt("id");
                }

                userExistsStmt.setString(1, user[2]);
                try (ResultSet userRs = userExistsStmt.executeQuery()) {
                    userRs.next();

                    if (userRs.getInt(1) > 0) {
                        continue;
                    }
                }

                insertStmt.setInt(1, employeeId);
                insertStmt.setString(2, user[2]);
                insertStmt.setString(3, PasswordUtils.hashPassword("1234"));
                insertStmt.executeUpdate();
            }
        }
    }

}
