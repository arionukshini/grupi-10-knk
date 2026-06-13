package com.company.system.service;

import com.company.system.exceptions.InvalidEmailException;
import com.company.system.exceptions.InvalidSalaryException;
import com.company.system.model.Employee;
import com.company.system.repository.EmployeeRepository;
import com.company.system.repository.jdbc.JdbcEmployeeRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.company.system.utils.Validator.emailValidator;
import static com.company.system.utils.Validator.salaryValidator;

public class EmployeeService {

    private static final EmployeeRepository employeeRepository = new JdbcEmployeeRepository();

    public static List<Employee> getAllEmployees() {
        try {
            return employeeRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean addEmployee(Employee employee) {
        if (!isValidEmployee(employee, "exception.employee.add")) {
            return false;
        }

        try {
            return employeeRepository.save(employee);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateEmployee(Employee employee) {
        if (!isValidEmployee(employee, "exception.employee.update")) {
            return false;
        }

        try {
            return employeeRepository.update(employee);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteEmployee(int employeeId) {
        try {
            return employeeRepository.deleteById(employeeId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Employee getEmployeeById(int employeeId) {
        try {
            return employeeRepository.findById(employeeId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Employee> getEmployeesByDepartment(int departmentId, Integer excludeEmployeeId) {
        try {
            return employeeRepository.findByDepartmentId(departmentId, excludeEmployeeId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static boolean isValidEmployee(Employee employee, String emailContextKey) {
        try {
            if (!emailValidator(employee.getEmail())) {
                throw new InvalidEmailException(employee.getEmail(), emailContextKey);
            }
            if (!salaryValidator(employee.getBaseSalary())) {
                throw new InvalidSalaryException(employee.getBaseSalary(), "employees.baseSalary");
            }
            return true;
        } catch (InvalidEmailException e) {
            e.showAlert();
            System.out.println(e.getMessage());
        } catch (InvalidSalaryException e) {
            e.showAlert();
            System.out.println(e.getMessage());
        }
        return false;
    }
}
