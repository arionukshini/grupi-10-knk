package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    // LOGIN
    public static User login(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.connect()) {

            if (conn == null) return null;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // REGISTER
    public static boolean register(String username, String password) {

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DBConnection.connect()) {

            if (conn == null) return false;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}