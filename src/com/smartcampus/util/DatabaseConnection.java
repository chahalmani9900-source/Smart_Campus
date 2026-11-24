package com.smartcampus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages JDBC connection lifecycle using DriverManager.
 * Provides clear configuration constants and connection testing.
 * Falls back to CSV mode when MySQL is not available.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/smart_campus_db?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "password";

    private static boolean mysqlAvailable = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection testConn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                if (testConn != null && !testConn.isClosed()) {
                    mysqlAvailable = true;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            mysqlAvailable = false;
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean isMysqlAvailable() {
        return mysqlAvailable;
    }

    public static String getConnectionStatusMessage() {
        if (mysqlAvailable) {
            return "[PERSISTENCE MODE: MySQL JDBC Connected -> " + URL + "]";
        }
        return "[PERSISTENCE MODE: CSV Local File Storage (MySQL server not detected on localhost:3306)]";
    }
}
