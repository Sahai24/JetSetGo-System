package util;

import java.sql.*;

/**
 * Initializes the database schema and seeds sample data on first run.
 */
public class DBInitializer {

    public static void initialize() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // ── Tables ──────────────────────────────────────────────────────

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS passengers (
                    id          INT AUTO_INCREMENT PRIMARY KEY,
                    name        VARCHAR(200) NOT NULL,
                    email       VARCHAR(200) UNIQUE NOT NULL,
                    phone       VARCHAR(20)  NOT NULL,
                    passport_no VARCHAR(50)  UNIQUE NOT NULL,
                    created_at  VARCHAR(30)  DEFAULT FORMATDATETIME(NOW(), 'yyyy-MM-dd HH:mm:ss')
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS flights (
                    id               INT AUTO_INCREMENT PRIMARY KEY,
                    flight_number    VARCHAR(20)  UNIQUE NOT NULL,
                    origin           VARCHAR(100) NOT NULL,
                    destination      VARCHAR(100) NOT NULL,
                    departure_time   VARCHAR(30)  NOT NULL,
                    arrival_time     VARCHAR(30)  NOT NULL,
                    total_seats      INT          NOT NULL,
                    available_seats  INT          NOT NULL,
                    price            DOUBLE       NOT NULL,
                    status           VARCHAR(20)  DEFAULT 'SCHEDULED'
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS reservations (
                    id              INT AUTO_INCREMENT PRIMARY KEY,
                    booking_ref     VARCHAR(20)  UNIQUE NOT NULL,
                    passenger_id    INT          NOT NULL,
                    flight_id       INT          NOT NULL,
                    seat_number     VARCHAR(10)  NOT NULL,
                    booking_date    VARCHAR(30)  DEFAULT FORMATDATETIME(NOW(), 'yyyy-MM-dd HH:mm:ss'),
                    status          VARCHAR(20)  DEFAULT 'CONFIRMED',
                    payment_status  VARCHAR(20)  DEFAULT 'PAID',
                    FOREIGN KEY (passenger_id) REFERENCES passengers(id),
                    FOREIGN KEY (flight_id)    REFERENCES flights(id)
                )
            """);

            // ── Seed flights (only once) ──────────────────────────────────
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM flights");
            if (rs.next() && rs.getInt(1) == 0) {
                String[][] flights = {
                    {"AI-101", "Delhi",     "Mumbai",    "2025-06-01 06:00", "2025-06-01 08:00",  "150", "150", "4500.00"},
                    {"AI-202", "Mumbai",    "Bangalore", "2025-06-01 09:00", "2025-06-01 10:30",  "120", "120", "3800.00"},
                    {"AI-303", "Bangalore", "Chennai",   "2025-06-01 11:00", "2025-06-01 12:00",  "100", "100", "2500.00"},
                    {"AI-404", "Chennai",   "Kolkata",   "2025-06-01 13:00", "2025-06-01 15:30",  "180", "180", "5200.00"},
                    {"AI-505", "Kolkata",   "Delhi",     "2025-06-01 16:00", "2025-06-01 18:30",  "200", "200", "4800.00"},
                    {"AI-606", "Delhi",     "Hyderabad", "2025-06-02 07:00", "2025-06-02 09:00",  "160", "160", "4200.00"},
                    {"AI-707", "Hyderabad", "Pune",      "2025-06-02 10:00", "2025-06-02 11:30",  "130", "130", "3500.00"},
                    {"AI-808", "Mumbai",    "Delhi",     "2025-06-02 14:00", "2025-06-02 16:00",  "150", "150", "4600.00"},
                };
                for (String[] f : flights) {
                    stmt.executeUpdate(String.format(
                        "INSERT INTO flights(flight_number,origin,destination,departure_time," +
                        "arrival_time,total_seats,available_seats,price) VALUES('%s','%s','%s','%s','%s',%s,%s,%s)",
                        f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7]));
                }
                System.out.println("✅ Sample flights seeded successfully.");
            }

            System.out.println("✅ Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("❌ DB Initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
