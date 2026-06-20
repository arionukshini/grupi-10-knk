package com.company.system.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

final class DatabaseConnector {

    private final DatabaseConfig config;

    DatabaseConnector(DatabaseConfig config) {
        this.config = config;
    }

    Connection connectToServer() throws SQLException {
        return DriverManager.getConnection(config.rootUrl(), config.username(), config.password());
    }

    Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(config.databaseUrl(), config.username(), config.password());
    }
}
