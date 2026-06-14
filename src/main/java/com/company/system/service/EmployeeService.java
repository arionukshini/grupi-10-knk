package com.company.system.service;


import com.company.system.utils.AppLogger;
import com.company.system.exceptions.InvalidEmailException;
import com.company.system.exceptions.InvalidSalaryException;
import com.company.system.models.Employee;
import com.company.system.repository.EmployeeRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.company.system.utils.Validator.emailValidator;
import static com.company.system.utils.Validator.employeeNameValidator;
import static com.company.system.utils.Validator.hireDateValidator;
import static com.company.system.utils.Validator.isPositive;
import static com.company.system.utils.Validator.phoneValidator;
import static com.company.system.utils.Validator.positionValidator;
import static com.company.system.utils.Validator.salaryValidator;
import static com.company.system.utils.Validator.statusValidator;

public class EmployeeService {

    private static final EmployeeRepository employeeRepository = new EmployeeRepository();

    public static List<Employee> getAllEmployees() {
        try {
            return employeeRepository.findAll();
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
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
            AppLogger.error("Failed to add employee", e);
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
            AppLogger.error("Failed to update employee", e);
            return false;
        }
    }

    public static boolean deleteEmployee(int employeeId) {
        try {
            return employeeRepository.deleteById(employeeId);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return false;
        }
    }

    public static Employee getEmployeeById(int employeeId) {
        try {
            return employeeRepository.findById(employeeId);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return null;
        }
    }

    public static List<Employee> getEmployeesByDepartment(int departmentId, Integer excludeEmployeeId) {
        try {
            return employeeRepository.findByDepartmentId(departmentId, excludeEmployeeId);
        } catch (SQLException e) {
            AppLogger.error("Unexpected error", e);
            return new ArrayList<>();
        }
    }

    private static boolean isValidEmployee(Employee employee, String emailContextKey) {
        try {
            if (!employeeNameValidator(employee.getFirstName())
                    || !employeeNameValidator(employee.getLastName())
                    || !phoneValidator(employee.getPhone())
                    || !positionValidator(employee.getPosition())
                    || !isPositive(employee.getDepartmentId())
                    || !hireDateValidator(employee.getHireDate())
                    || !statusValidator(employee.getStatus())) {
                AppLogger.info("Invalid employee data rejected.");
                return false;
            }
            if (!emailValidator(employee.getEmail())) {
                throw new InvalidEmailException(employee.getEmail(), emailContextKey);
            }
            if (!salaryValidator(employee.getBaseSalary())) {
                throw new InvalidSalaryException(employee.getBaseSalary(), "employees.baseSalary");
            }
            return true;
        } catch (InvalidEmailException e) {
            e.showAlert();
            AppLogger.error("Invalid employee email", e);
        } catch (InvalidSalaryException e) {
            e.showAlert();
            AppLogger.error("Invalid employee salary", e);
        }
        return false;
    }
}
