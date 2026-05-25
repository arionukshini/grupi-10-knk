package com.company.system.db;

import java.sql.*;
import com.company.system.utils.PasswordUtils;

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
                    
                        contract_type VARCHAR(50),
                    
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
                    
                        amount DECIMAL(10,2),
                        
                        bonus DECIMAL(10,2),
                        
                        deductions DECIMAL(10,2),
                    
                        payment_date DATE,
                    
                        FOREIGN KEY (employee_id)
                        REFERENCES employees(id)
                    )
                    """);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                    
                        username VARCHAR(50) UNIQUE NOT NULL,
                        password_hash VARCHAR(255) NOT NULL,
                        role VARCHAR(20) NOT NULL DEFAULT 'USER',
                    
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
                        
                         ('Sara', 'Berisha', 'sara.berisha@company.com', '+38344111333', 'HR Manager', 1, '2023-06-10', 1100.00, 'Active'),
                        
                         ('Leon', 'Krasniqi', 'leon.krasniqi@company.com', '+38344111444', 'Accountant', 2, '2022-09-01', 1000.00, 'Active'),
                        
                         ('Diona', 'Gashi', 'diona.gashi@company.com', '+38344111555', 'Marketing Specialist', 4, '2024-03-20', 950.00, 'Active'),
                        
                         ('Albin', 'Rexhepi', 'albin.rexhepi@company.com', '+38344111666', 'Sales Representative', 5, '2023-11-05', 900.00, 'Inactive'),
                        
                         ('Era', 'Hasani', 'era.hasani@company.com', '+38344111777', 'System Administrator', 3, '2021-12-12', 1300.00, 'Active'),
                        
                         ('Blend', 'Shala', 'blend.shala@company.com', '+38344111888', 'Recruiter', 1, '2024-04-01', 850.00, 'Pending');
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
                         
                         (6, 'Full-Time', '2021-12-12', '2026-12-12', 1300.00, 'Active'),
                         
                         (7, 'Temporary', '2024-04-01', '2024-10-01', 850.00, 'Pending');
                        """);

                stmt.executeUpdate("""
                        INSERT INTO salaries
                         (employee_id, amount, bonus, deductions, payment_date)
                         VALUES
                         (1, 1200.00, 100.00, 50.00, '2026-05-01'),
                         
                         (2, 1100.00, 50.00, 20.00, '2026-05-01'),
                         
                         (3, 1000.00, 0.00, 30.00, '2026-05-01'),
                         
                         (4, 950.00, 25.00, 15.00, '2026-05-01'),
                         
                         (5, 900.00, 0.00, 10.00, '2026-05-01'),
                         
                         (6, 1300.00, 150.00, 60.00, '2026-05-01'),
                         
                         (7, 850.00, 0.00, 0.00, '2026-05-01');
                        """);

                System.out.println("Demo data inserted!");
            }

            seedDefaultAdmin(conn);
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
        String insertSql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        try (Statement countStmt = conn.createStatement();
             ResultSet rs = countStmt.executeQuery(countSql)) {

            rs.next();

            if (rs.getInt(1) > 0) {
                return;
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, "admin");
            stmt.setString(2, PasswordUtils.hashPassword("admin123"));
            stmt.setString(3, "ADMIN");
            stmt.executeUpdate();
        }
    }
}