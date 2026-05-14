package com.company.system.db;

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
                    
                        name VARCHAR(100) NOT NULL,
                        location VARCHAR(100)
                    )
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
                    
                        base_salary DOUBLE,
                    
                        status VARCHAR(20),
                    
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
                    
                        status VARCHAR(20),
                    
                        FOREIGN KEY (employee_id)
                        REFERENCES employees(id)
                    )
                    """);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS salaries (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                    
                        employee_id INT NOT NULL,
                    
                        amount DOUBLE,
                    
                        bonus DOUBLE,
                    
                        deductions DOUBLE,
                    
                        payment_date DATE,
                    
                        FOREIGN KEY (employee_id)
                        REFERENCES employees(id)
                    )
                    """);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                    
                        username VARCHAR(50) UNIQUE NOT NULL,
                        password VARCHAR(100) NOT NULL
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
                        INSERT INTO departments (name, location)
                        VALUES
                        ('IT', 'Prishtina'),
                        ('Human Resources', 'Prizren')
                        """);

                stmt.executeUpdate("""
                        INSERT INTO employees
                        (
                            first_name,
                            last_name,
                            email,
                            phone,
                            position,
                            department_id,
                            hire_date,
                            base_salary,
                            status
                        )
                        VALUES
                        (
                            'Arion',
                            'Ukshini',
                            'arion.ukshini@company.com',
                            '049123123',
                            'Software Developer',
                            1,
                            '2023-06-15',
                            1500,
                            'Active'
                        ),
                        (
                            'Sara',
                            'Berisha',
                            'sara.berisha@company.com',
                            '048555111',
                            'Backend Developer',
                            1,
                            '2022-03-10',
                            1400,
                            'Active'
                        ),
                        (
                            'Leon',
                            'Krasniqi',
                            'leon.krasniqi@company.com',
                            '045777222',
                            'HR Manager',
                            2,
                            '2021-11-01',
                            1300,
                            'Active'
                        )
                        """);

                stmt.executeUpdate("""
                        INSERT INTO contracts
                        (
                            employee_id,
                            contract_type,
                            start_date,
                            end_date,
                            salary,
                            status
                        )
                        VALUES
                        (
                            1,
                            'Full-Time',
                            '2023-06-15',
                            '2026-06-15',
                            1500,
                            'Active'
                        ),
                        (
                            2,
                            'Remote',
                            '2022-03-10',
                            '2025-03-10',
                            1400,
                            'Active'
                        ),
                        (
                            3,
                            'Part-Time',
                            '2021-11-01',
                            '2024-11-01',
                            1300,
                            'Expired'
                        )
                        """);

                stmt.executeUpdate("""
                        INSERT INTO salaries
                        (
                            employee_id,
                            amount,
                            bonus,
                            deductions,
                            payment_date
                        )
                        VALUES
                        (
                            1,
                            1500,
                            200,
                            50,
                            '2025-05-01'
                        ),
                        (
                            2,
                            1400,
                            150,
                            25,
                            '2025-05-01'
                        ),
                        (
                            3,
                            1300,
                            100,
                            75,
                            '2025-05-01'
                        )
                        """);

                System.out.println("Demo data inserted!");
            }

            conn.close();

            System.out.println(
                    "Database initialized successfully!"
            );

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}