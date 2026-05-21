package com.company.system.service;

import com.company.system.db.DBConnection;
import com.company.system.model.User;
import com.company.system.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    public static User login(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? ";

        try (Connection conn = DBConnection.connect()) {

            if (conn == null || username == null || username.isBlank() || password == null || password.isBlank()) {
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username.trim());

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");

                    if (PasswordUtils.verifyPassword(password, storedHash)) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                storedHash,
                                rs.getString("role"),
                                rs.getTimestamp("created_at")
                        );
                    }

                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean register(String username, String password) {

        String sql ="INSERT INTO users (username,password_hash, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.connect()) {

            if (conn == null || username == null || username.isBlank() || password == null || password.isBlank()) {
                return false;
            }
            if (userExists(username, conn)) {
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username.trim());
                stmt.setString(2, PasswordUtils.hashPassword(password));
                stmt.setString(3, "USER");

                return stmt.executeUpdate() > 0;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean resetPassword(String username, String newPassword) {

        String sql = "UPDATE users SET password_hash = ? WHERE username = ?";

        try (Connection conn = DBConnection.connect()) {

            if (conn == null || username == null || username.isBlank() || newPassword == null || newPassword.isBlank()) {
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, PasswordUtils.hashPassword(newPassword));
                stmt.setString(2, username.trim());

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
        public static String getPasswordHashByUsername(String username) {

            String sql = "SELECT password_hash FROM users WHERE username = ?";

            try (Connection conn = DBConnection.connect()) {

                if (conn == null || username == null || username.isBlank()) {
                    return null;
                }

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username.trim());

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        return rs.getString("password_hash");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }
    public static boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DBConnection.connect()) {

            if (conn == null || username == null || username.isBlank()) {
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username.trim());

                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
        private static boolean userExists(String username, Connection conn) throws SQLException {
            String sql = "SELECT 1 FROM users WHERE username = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username.trim());

                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        }
}