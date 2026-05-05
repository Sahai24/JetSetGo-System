package dao;

import model.Reservation;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Reservation operations via JDBC.
 */
public class ReservationDAO {

    private static final String JOIN_SQL = """
        SELECT r.*, p.name AS passenger_name,
               f.flight_number, f.origin, f.destination,
               f.departure_time, f.price
        FROM reservations r
        JOIN passengers p ON r.passenger_id = p.id
        JOIN flights    f ON r.flight_id    = f.id
    """;

    // ── Create ─────────────────────────────────────────────────────────────

    public boolean addReservation(Reservation r) {
        String sql = """
            INSERT INTO reservations(booking_ref, passenger_id, flight_id,
                                     seat_number, status, payment_status)
            VALUES(?,?,?,?,?,?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getBookingRef());
            ps.setInt   (2, r.getPassengerId());
            ps.setInt   (3, r.getFlightId());
            ps.setString(4, r.getSeatNumber());
            ps.setString(5, r.getStatus() != null ? r.getStatus() : "CONFIRMED");
            ps.setString(6, r.getPaymentStatus() != null ? r.getPaymentStatus() : "PAID");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs2 = ps.getGeneratedKeys()) {
                    if (rs2.next()) r.setId(rs2.getInt(1));
                }
            }
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ addReservation: " + e.getMessage());
            return false;
        }
    }

    // ── Read ───────────────────────────────────────────────────────────────

    public Reservation getReservationById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN_SQL + " WHERE r.id = ?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("❌ getReservationById: " + e.getMessage());
        }
        return null;
    }

    public Reservation getReservationByRef(String ref) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN_SQL + " WHERE r.booking_ref = ?")) {

            ps.setString(1, ref);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("❌ getReservationByRef: " + e.getMessage());
        }
        return null;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(JOIN_SQL + " ORDER BY r.booking_date DESC")) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ getAllReservations: " + e.getMessage());
        }
        return list;
    }

    public List<Reservation> getReservationsByPassenger(int passengerId) {
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     JOIN_SQL + " WHERE r.passenger_id = ? ORDER BY r.booking_date DESC")) {

            ps.setInt(1, passengerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ getReservationsByPassenger: " + e.getMessage());
        }
        return list;
    }

    public List<Reservation> getReservationsByFlight(int flightId) {
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     JOIN_SQL + " WHERE r.flight_id = ? AND r.status = 'CONFIRMED'")) {

            ps.setInt(1, flightId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ getReservationsByFlight: " + e.getMessage());
        }
        return list;
    }

    /** Returns seats already taken on a flight (for seat-number uniqueness check). */
    public List<String> getBookedSeats(int flightId) {
        List<String> seats = new ArrayList<>();
        String sql = "SELECT seat_number FROM reservations WHERE flight_id = ? AND status = 'CONFIRMED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, flightId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) seats.add(rs.getString("seat_number"));

        } catch (SQLException e) {
            System.err.println("❌ getBookedSeats: " + e.getMessage());
        }
        return seats;
    }

    // ── Update ─────────────────────────────────────────────────────────────

    public boolean cancelReservation(String bookingRef) {
        String sql = "UPDATE reservations SET status = 'CANCELLED' WHERE booking_ref = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, bookingRef);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ cancelReservation: " + e.getMessage());
            return false;
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setBookingRef(rs.getString("booking_ref"));
        r.setPassengerId(rs.getInt("passenger_id"));
        r.setFlightId(rs.getInt("flight_id"));
        r.setSeatNumber(rs.getString("seat_number"));
        r.setBookingDate(rs.getString("booking_date"));
        r.setStatus(rs.getString("status"));
        r.setPaymentStatus(rs.getString("payment_status"));
        // Joined fields
        r.setPassengerName(rs.getString("passenger_name"));
        r.setFlightNumber(rs.getString("flight_number"));
        r.setOrigin(rs.getString("origin"));
        r.setDestination(rs.getString("destination"));
        r.setDepartureTime(rs.getString("departure_time"));
        r.setPrice(rs.getDouble("price"));
        return r;
    }
}
