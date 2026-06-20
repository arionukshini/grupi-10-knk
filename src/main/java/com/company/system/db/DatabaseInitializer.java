package com.company.system.db;

import com.company.system.utils.AppLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    static void initialize(DatabaseConfig config) {
        DatabaseConnector connector = new DatabaseConnector(config);

        try {
            createDatabase(connector, config);

            try (Connection connection = connector.connectToDatabase()) {
                DatabaseSchema.createTables(connection);
                DatabaseSchema.applyMigrations(connection);
                DemoDataSeeder.seed(connection);
            }

            AppLogger.info("Database initialized successfully.");
        } catch (Exception e) {
            AppLogger.error("Database initialization failed", e);
        }
    }

    private static void createDatabase(DatabaseConnector connector, DatabaseConfig config) throws SQLException {
        try (
                Connection rootConnection = connector.connectToServer();
                Statement rootStatement = rootConnection.createStatement()
        ) {
            rootStatement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + config.database());
        }
    }
}
