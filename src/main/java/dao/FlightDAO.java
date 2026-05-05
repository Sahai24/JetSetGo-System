package dao;

import model.Flight;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Flight operations via JDBC.
 */
public class FlightDAO {

    // ── Create ─────────────────────────────────────────────────────────────

    public boolean addFlight(Flight f) {
        String sql = """
            INSERT INTO flights(flight_number,origin,destination,departure_time,
                                arrival_time,total_seats,available_seats,price,status)
            VALUES(?,?,?,?,?,?,?,?,?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, f.getFlightNumber());
            ps.setString(2, f.getOrigin());
            ps.setString(3, f.getDestination());
            ps.setString(4, f.getDepartureTime());
            ps.setString(5, f.getArrivalTime());
            ps.setInt   (6, f.getTotalSeats());
            ps.setInt   (7, f.getAvailableSeats());
            ps.setDouble(8, f.getPrice());
            ps.setString(9, f.getStatus() != null ? f.getStatus() : "SCHEDULED");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) f.setId(rs.getInt(1));
                }
            }
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ addFlight: " + e.getMessage());
            return false;
        }
    }

    // ── Read ───────────────────────────────────────────────────────────────

    public Flight getFlightById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM flights WHERE id = ?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("❌ getFlightById: " + e.getMessage());
        }
        return null;
    }

    public Flight getFlightByNumber(String number) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM flights WHERE flight_number = ?")) {

            ps.setString(1, number);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("❌ getFlightByNumber: " + e.getMessage());
        }
        return null;
    }

    public List<Flight> getAllFlights() {
        List<Flight> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM flights ORDER BY departure_time")) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ getAllFlights: " + e.getMessage());
        }
        return list;
    }

    public List<Flight> searchFlights(String origin, String destination) {
        List<Flight> list = new ArrayList<>();
        String sql = """
            SELECT * FROM flights
            WHERE LOWER(origin) = LOWER(?)
              AND LOWER(destination) = LOWER(?)
              AND available_seats > 0
              AND status = 'SCHEDULED'
            ORDER BY departure_time
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, origin);
            ps.setString(2, destination);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ searchFlights: " + e.getMessage());
        }
        return list;
    }

    public List<Flight> getAvailableFlights() {
        List<Flight> list = new ArrayList<>();
        String sql = "SELECT * FROM flights WHERE available_seats > 0 AND status = 'SCHEDULED' ORDER BY departure_time";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ getAvailableFlights: " + e.getMessage());
        }
        return list;
    }

    // ── Update ─────────────────────────────────────────────────────────────

    public boolean updateFlight(Flight f) {
        String sql = """
            UPDATE flights SET flight_number=?, origin=?, destination=?,
                departure_time=?, arrival_time=?, total_seats=?,
                available_seats=?, price=?, status=?
            WHERE id=?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, f.getFlightNumber());
            ps.setString(2, f.getOrigin());
            ps.setString(3, f.getDestination());
            ps.setString(4, f.getDepartureTime());
            ps.setString(5, f.getArrivalTime());
            ps.setInt   (6, f.getTotalSeats());
            ps.setInt   (7, f.getAvailableSeats());
            ps.setDouble(8, f.getPrice());
            ps.setString(9, f.getStatus());
            ps.setInt  (10, f.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ updateFlight: " + e.getMessage());
            return false;
        }
    }

    public boolean decrementSeat(int flightId) {
        String sql = "UPDATE flights SET available_seats = available_seats - 1 WHERE id = ? AND available_seats > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, flightId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ decrementSeat: " + e.getMessage());
            return false;
        }
    }

    public boolean incrementSeat(int flightId) {
        String sql = "UPDATE flights SET available_seats = available_seats + 1 WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, flightId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ incrementSeat: " + e.getMessage());
            return false;
        }
    }

    // ── Delete ─────────────────────────────────────────────────────────────

    public boolean deleteFlight(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM flights WHERE id = ?")) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ deleteFlight: " + e.getMessage());
            return false;
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private Flight mapRow(ResultSet rs) throws SQLException {
        Flight f = new Flight();
        f.setId(rs.getInt("id"));
        f.setFlightNumber(rs.getString("flight_number"));
        f.setOrigin(rs.getString("origin"));
        f.setDestination(rs.getString("destination"));
        f.setDepartureTime(rs.getString("departure_time"));
        f.setArrivalTime(rs.getString("arrival_time"));
        f.setTotalSeats(rs.getInt("total_seats"));
        f.setAvailableSeats(rs.getInt("available_seats"));
        f.setPrice(rs.getDouble("price"));
        f.setStatus(rs.getString("status"));
        return f;
    }
}
