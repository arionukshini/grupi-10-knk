package com.company.system.repository;

import com.company.system.db.DBConnection;
import com.company.system.models.mappers.EmployeeMapper;
import com.company.system.models.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository extends BaseRepository<Employee> {

    private final EmployeeMapper mapper = new EmployeeMapper();
    @Override
    public List<Employee> findAll() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                employees.add(mapper.fromResultSet(rs));
            }
        }

        return employees;
    }
    @Override
    public Employee findById(int employeeId) throws SQLException {
        String sql = """
                SELECT *
                FROM employees
                WHERE id = ?
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.fromResultSet(rs);
                }
            }
        }

        return null;
    }
    public List<Employee> findByDepartmentId(int departmentId, Integer excludeEmployeeId) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = """
                SELECT *
                FROM employees
                WHERE department_id = ?
                ORDER BY last_name, first_name
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, departmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int employeeId = rs.getInt("id");
                    if (excludeEmployeeId != null && employeeId == excludeEmployeeId) {
                        continue;
                    }
                    employees.add(mapper.fromResultSet(rs));
                }
            }
        }

        return employees;
    }
    @Override
    public boolean save(Employee employee) throws SQLException {
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
            bindEmployee(stmt, employee);
            return stmt.executeUpdate() > 0;
        }
    }
    @Override
    public boolean update(Employee employee) throws SQLException {
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
            bindEmployee(stmt, employee);
            stmt.setInt(10, employee.getId());
            return stmt.executeUpdate() > 0;
        }
    }
    @Override
    public boolean deleteById(int employeeId) throws SQLException {
        String deleteSalaryHistorySql = """
                DELETE FROM salary_history
                WHERE employee_id = ?
                   OR salary_id IN (
                       SELECT id
                       FROM salaries
                       WHERE employee_id = ?
                   )
                """;

        String deleteSalariesSql = "DELETE FROM salaries WHERE employee_id = ?";
        String deleteContractsSql = "DELETE FROM contracts WHERE employee_id = ?";
        String deleteVacationRequestsSql = "DELETE FROM vacation_requests WHERE employee_id = ?";
        String deleteEmployeeSql = "DELETE FROM employees WHERE id = ?";

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                return false;
            }

            conn.setAutoCommit(false);

            try (
                    PreparedStatement historyStmt = conn.prepareStatement(deleteSalaryHistorySql);
                    PreparedStatement salariesStmt = conn.prepareStatement(deleteSalariesSql);
                    PreparedStatement contractsStmt = conn.prepareStatement(deleteContractsSql);
                    PreparedStatement vacationRequestsStmt = conn.prepareStatement(deleteVacationRequestsSql);
                    PreparedStatement employeeStmt = conn.prepareStatement(deleteEmployeeSql)
            ) {
                historyStmt.setInt(1, employeeId);
                historyStmt.setInt(2, employeeId);
                historyStmt.executeUpdate();

                salariesStmt.setInt(1, employeeId);
                salariesStmt.executeUpdate();

                contractsStmt.setInt(1, employeeId);
                contractsStmt.executeUpdate();

                vacationRequestsStmt.setInt(1, employeeId);
                vacationRequestsStmt.executeUpdate();

                employeeStmt.setInt(1, employeeId);
                boolean deleted = employeeStmt.executeUpdate() > 0;

                if (deleted) {
                    conn.commit();
                } else {
                    conn.rollback();
                }

                return deleted;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private void bindEmployee(PreparedStatement stmt, Employee employee) throws SQLException {
        stmt.setString(1, employee.getFirstName());
        stmt.setString(2, employee.getLastName());
        stmt.setString(3, employee.getEmail());
        stmt.setString(4, employee.getPhone());
        stmt.setString(5, employee.getPosition());
        stmt.setInt(6, employee.getDepartmentId());
        stmt.setDate(7, employee.getHireDate());
        stmt.setDouble(8, employee.getBaseSalary());
        stmt.setString(9, employee.getStatus());
    }
}
