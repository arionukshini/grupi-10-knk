package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.exceptions.InvalidEmailException;
import com.company.system.exceptions.InvalidSalaryException;
import com.company.system.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.company.system.utils.Validator.emailValidator;
import static com.company.system.utils.Validator.salaryValidator;

public class EmployeeService {

    public static List<Employee> getAllEmployees() {

        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM employees";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                Employee employee = new Employee(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("position"),
                        rs.getInt("department_id"),
                        rs.getDate("hire_date"),
                        rs.getDouble("base_salary"),
                        rs.getString("status")
                );

                employees.add(employee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    public static boolean addEmployee(Employee employee) {

        String sql = """
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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            if (!emailValidator(employee.getEmail())) {
                throw new InvalidEmailException(employee.getEmail(), "Punetori nuk u shtua.");
            }
            if (!salaryValidator(employee.getBaseSalary())) {
                throw new InvalidSalaryException(employee.getBaseSalary(), "Punetori nuk u shtua.");
            }

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPhone());
            stmt.setString(5, employee.getPosition());
            stmt.setInt(6, employee.getDepartmentId());
            stmt.setDate(7, employee.getHireDate());
            stmt.setDouble(8, employee.getBaseSalary());
            stmt.setString(9, employee.getStatus());

            stmt.executeUpdate();

            System.out.println("Employee added successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateEmployee(Employee employee) {

        String sql = """
                UPDATE employees
                SET
                    first_name = ?,
                    last_name = ?,
                    email = ?,
                    phone = ?,
                    position = ?,
                    department_id = ?,
                    hire_date = ?,
                    base_salary = ?,
                    status = ?
                WHERE id = ?
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            if (!emailValidator(employee.getEmail())) {
                throw new InvalidEmailException(employee.getEmail(), "Punetori nuk u perditesua.");
            }
            if (!salaryValidator(employee.getBaseSalary())) {
                throw new InvalidSalaryException(employee.getBaseSalary(), "Punetori nuk u perditesua.");
            }

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPhone());
            stmt.setString(5, employee.getPosition());
            stmt.setInt(6, employee.getDepartmentId());
            stmt.setDate(7, employee.getHireDate());
            stmt.setDouble(8, employee.getBaseSalary());
            stmt.setString(9, employee.getStatus());
            stmt.setInt(10, employee.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteEmployee(int employeeId) {

        String sql = "DELETE FROM employees WHERE id = ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}