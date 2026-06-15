package com.company.system.db;

import com.company.system.utils.AppLogger;

import java.sql.Connection;
import java.sql.SQLException;

public final class DBConnection {

    private static final DatabaseConfig CONFIG = DatabaseConfig.fromEnvironment();
    private static final DatabaseConnector CONNECTOR = new DatabaseConnector(CONFIG);

    private DBConnection() {
    }

    public static Connection connect() {
        try {
            return CONNECTOR.connectToDatabase();
        } catch (SQLException e) {
            AppLogger.error("Database connection failed", e);
            return null;
        }
    }

    public static void initializeDatabase() {
        DatabaseInitializer.initialize(CONFIG);
    }
}
