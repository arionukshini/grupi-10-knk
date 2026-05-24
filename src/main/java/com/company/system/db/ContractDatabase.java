package com.company.system.db;

import models.Contract;
import java.sql.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ContractDatabase {

  
    public static ObservableList<Contract> getAllContracts() {
        ObservableList<Contract> list = FXCollections.observableArrayList();
        
      
        String query = "SELECT c.contract_id, c.employee_id, c.status, " +
                       "CONCAT(e.first_name, ' ', e.last_name) AS full_name " +
                       "FROM contracts c " +
                       "JOIN employees e ON c.employee_id = e.employee_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new Contract(
                    rs.getInt("contract_id"),
                    rs.getInt("employee_id"),
                    rs.getString("full_name"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    
    public static boolean addContract(int employeeId, String status) {
        String query = "INSERT INTO contracts (employee_id, status) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, status);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public static boolean updateContract(int contractId, String status) {
        String query = "UPDATE contracts SET status = ? WHERE contract_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, contractId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

  
    public static boolean deleteContract(int contractId) {
        String query = "DELETE FROM contracts WHERE contract_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, contractId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}