package com.hms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * GoF SINGLETON PATTERN
 * ─────────────────────
 * Ensures only ONE database connection exists across the entire application.
 * This conserves resources and prevents inconsistent state.
 *
 * Usage:
 *   Connection conn = DatabaseConnection.getInstance().getConnection();
 */
public class DatabaseConnection {

    // ── Configuration ─────────────────────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/hms_db"
                                         + "?useSSL=false&allowPublicKeyRetrieval=true"
                                         + "&serverTimezone=UTC";
    private static final String DB_USER  = "root";
    private static final String DB_PASS  = "Shifaimran1"; // ← change to your MySQL password

    // ── Singleton State ───────────────────────────────────────
    private static DatabaseConnection instance;
    private Connection connection;

    // ── Private Constructor (Singleton) ───────────────────────
    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, DB_USER, DB_PASS);
            System.out.println("[DB] Connection established successfully.");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * Returns the single instance of DatabaseConnection.
     * Thread-safe via synchronized keyword.
     *
     * @return DatabaseConnection instance
     * @throws RuntimeException if the connection cannot be established
     */
    public static synchronized DatabaseConnection getInstance() {
        try {
            if (instance == null || isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("[DB] Failed to obtain connection: " + e.getMessage(), e);
        }
        return instance;
    }

    /**
     * Returns the active JDBC Connection object.
     *
     * @return java.sql.Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Checks whether the current connection is closed or null.
     */
    private static boolean isClosed() {
        try {
            return instance.connection == null || instance.connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    /**
     * Gracefully closes the connection. Call on application shutdown.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
