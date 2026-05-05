package util;

import java.sql.*;

/**
 * Manages JDBC database connections using H2 embedded database.
 * H2 is a pure-Java embedded database — no external server required.
 * Data is persisted to ./airline_reservation.mv.db
 */
public class DBConnection {

    // FILE mode keeps data across restarts; MODE=MySQL for familiar SQL syntax
    private static final String DB_URL  = "jdbc:h2:./airline_reservation;MODE=MySQL;AUTO_SERVER=FALSE";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                connection.setAutoCommit(true);
            } catch (ClassNotFoundException e) {
                throw new SQLException("H2 JDBC driver not found: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
