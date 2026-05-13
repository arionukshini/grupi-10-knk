package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.Contract;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractService {

    public static List<Contract> getAllContracts() {

        List<Contract> contracts = new ArrayList<>();

        String sql = "SELECT * FROM contracts";

        try (
                Connection conn = DBConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                Contract contract = new Contract(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getString("contract_type"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getDouble("salary"),
                        rs.getString("status")
                );

                contracts.add(contract);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contracts;
    }

    public static void addContract(Contract contract) {

        String sql = """
                INSERT INTO contracts
                (
                    employee_id,
                    contract_type,
                    start_date,
                    end_date,
                    salary,
                    status
                )
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

            stmt.executeUpdate();

            System.out.println("Contract added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}