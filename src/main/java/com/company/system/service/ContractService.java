package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.Contract;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractService {

    public static List<Contract> getAllContracts() {
        List<Contract> contracts = new ArrayList<>();

        String sql = """
                SELECT c.*, CONCAT(e.first_name, ' ', e.last_name) AS employee_name
                FROM contracts c
                JOIN employees e ON c.employee_id = e.id
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                contracts.add(new Contract(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getString("employee_name"),
                        rs.getString("contract_type"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getDouble("salary"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contracts;
    }

    public static List<Contract> getExpiringContractsForEmployee(int employeeId) {
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

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contracts.add(new Contract(
                            rs.getInt("id"),
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getString("contract_type"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getDouble("salary"),
                            rs.getString("status")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contracts;
    }

    public static boolean addContract(Contract contract) {
        String sql = """
                INSERT INTO contracts
                (employee_id, contract_type, start_date, end_date, salary, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contract.getEmployeeId());
            stmt.setString(2, contract.getContractType());
            stmt.setDate(3, contract.getStartDate());
            stmt.setDate(4, contract.getEndDate());
            stmt.setDouble(5, contract.getSalary());
            stmt.setString(6, contract.getStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateContract(Contract contract) {
        String sql = """
                UPDATE contracts
                SET employee_id = ?, contract_type = ?, start_date = ?, end_date = ?, salary = ?, status = ?
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contract.getEmployeeId());
            stmt.setString(2, contract.getContractType());
            stmt.setDate(3, contract.getStartDate());
            stmt.setDate(4, contract.getEndDate());
            stmt.setDouble(5, contract.getSalary());
            stmt.setString(6, contract.getStatus());
            stmt.setInt(7, contract.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Contract getLatestContractByEmployeeId(int employeeId) {
        String sql = """
                SELECT c.*, CONCAT(e.first_name, ' ', e.last_name) AS employee_name
                FROM contracts c
                JOIN employees e ON c.employee_id = e.id
                WHERE c.employee_id = ?
                ORDER BY COALESCE(c.end_date, c.start_date) DESC, c.id DESC
                LIMIT 1
                """;

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, employeeId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Contract(
                                rs.getInt("id"),
                                rs.getInt("employee_id"),
                                rs.getString("employee_name"),
                                rs.getString("contract_type"),
                                rs.getDate("start_date"),
                                rs.getDate("end_date"),
                                rs.getDouble("salary"),
                                rs.getString("status")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static boolean deleteContract(int id) {
        String sql = "DELETE FROM contracts WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public static Contract getLatestContractByEmployeeId(int employeeId) {
        String sql = """
        SELECT c.*, CONCAT(e.first_name, ' ', e.last_name) AS employee_name
        FROM contracts c
        JOIN employees e ON c.employee_id = e.id
        WHERE c.employee_id = ?
        ORDER BY c.start_date DESC
        LIMIT 1
        """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Contract(
                            rs.getInt("id"),
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getString("contract_type"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getDouble("salary"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}