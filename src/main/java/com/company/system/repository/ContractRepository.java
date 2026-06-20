package com.company.system.repository;

import com.company.system.db.DBConnection;
import com.company.system.models.mappers.ContractMapper;
import com.company.system.models.Contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContractRepository extends BaseRepository<Contract> {

    private final ContractMapper mapper = new ContractMapper();
    @Override
    public List<Contract> findAll() throws SQLException {
        List<Contract> contracts = new ArrayList<>();
        String sql = """
                SELECT c.*, CONCAT(e.first_name, ' ', e.last_name) AS employee_name
                FROM contracts c
                JOIN employees e ON c.employee_id = e.id
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                contracts.add(mapper.fromResultSet(rs));
            }
        }

        return contracts;
    }
    public List<Contract> findExpiringByEmployeeId(int employeeId) throws SQLException {
        List<Contract> contracts = new ArrayList<>();
        String sql = """
                SELECT c.*, CONCAT(e.first_name, ' ', e.last_name) AS employee_name
                FROM contracts c
                JOIN employees e ON c.employee_id = e.id
                WHERE c.employee_id = ?
                  AND c.status = 'Active'
                  AND c.end_date IS NOT NULL
                  AND c.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 14 DAY)
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contracts.add(mapper.fromResultSet(rs));
                }
            }
        }

        return contracts;
    }
    public Contract findLatestByEmployeeId(int employeeId) throws SQLException {
        String sql = """
                SELECT c.*, CONCAT(e.first_name, ' ', e.last_name) AS employee_name
                FROM contracts c
                JOIN employees e ON c.employee_id = e.id
                WHERE c.employee_id = ?
                ORDER BY COALESCE(c.end_date, c.start_date) DESC, c.id DESC
                LIMIT 1
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
    @Override
    public boolean save(Contract contract) throws SQLException {
        String sql = """
                INSERT INTO contracts
                (employee_id, contract_type, start_date, end_date, salary, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, contract.getEmployeeId());
            stmt.setString(2, contract.getContractType());
            stmt.setDate(3, contract.getStartDate());
            stmt.setDate(4, contract.getEndDate());
            stmt.setDouble(5, contract.getSalary());
            stmt.setString(6, contract.getStatus());
            return stmt.executeUpdate() > 0;
        }
    }
    @Override
    public boolean update(Contract contract) throws SQLException {
        String sql = """
                UPDATE contracts
                SET employee_id = ?, contract_type = ?, start_date = ?, end_date = ?, salary = ?, status = ?
                WHERE id = ?
                """;

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, contract.getEmployeeId());
            stmt.setString(2, contract.getContractType());
            stmt.setDate(3, contract.getStartDate());
            stmt.setDate(4, contract.getEndDate());
            stmt.setDouble(5, contract.getSalary());
            stmt.setString(6, contract.getStatus());
            stmt.setInt(7, contract.getId());
            return stmt.executeUpdate() > 0;
        }
    }
    @Override
    public boolean deleteById(int contractId) throws SQLException {
        String sql = "DELETE FROM contracts WHERE id = ?";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, contractId);
            return stmt.executeUpdate() > 0;
        }
    }
}
