package com.company.system.db;

final class DatabaseConfig {

    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;

    private DatabaseConfig(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    static DatabaseConfig fromEnvironment() {
        return new DatabaseConfig(
                envOrDefault("APP_DB_HOST", "localhost"),
                envOrDefault("APP_DB_PORT", "3306"),
                databaseName(envOrDefault("APP_DB_NAME", "grupi_10")),
                envOrDefault("APP_DB_USER", "root"),
                envOrDefault("APP_DB_PASSWORD", "pass")
        );
    }

    String database() {
        return database;
    }

    String username() {
        return username;
    }

    String password() {
        return password;
    }

    String rootUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/";
    }

    String databaseUrl() {
        return rootUrl() + database;
    }

    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static String databaseName(String value) {
        if (!value.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("Database name must contain only letters, numbers, and underscores.");
        }
        return value;
    }
}
