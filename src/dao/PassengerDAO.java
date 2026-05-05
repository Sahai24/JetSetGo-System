package dao;

import model.Passenger;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Passenger CRUD operations via JDBC.
 */
public class PassengerDAO {

    // ── Create ─────────────────────────────────────────────────────────────

    public boolean addPassenger(Passenger p) {
        String sql = "INSERT INTO passengers(name, email, phone, passport_no) VALUES(?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getEmail());
            ps.setString(3, p.getPhone());
            ps.setString(4, p.getPassportNo());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setId(rs.getInt(1));
                }
            }
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ addPassenger: " + e.getMessage());
            return false;
        }
    }

    // ── Read ───────────────────────────────────────────────────────────────

    public Passenger getPassengerById(int id) {
        String sql = "SELECT * FROM passengers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("❌ getPassengerById: " + e.getMessage());
        }
        return null;
    }

    public Passenger getPassengerByEmail(String email) {
        String sql = "SELECT * FROM passengers WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("❌ getPassengerByEmail: " + e.getMessage());
        }
        return null;
    }

    public Passenger getPassengerByPassport(String passport) {
        String sql = "SELECT * FROM passengers WHERE passport_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, passport);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("❌ getPassengerByPassport: " + e.getMessage());
        }
        return null;
    }

    public List<Passenger> getAllPassengers() {
        List<Passenger> list = new ArrayList<>();
        String sql = "SELECT * FROM passengers ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ getAllPassengers: " + e.getMessage());
        }
        return list;
    }

    public List<Passenger> searchPassengers(String keyword) {
        List<Passenger> list = new ArrayList<>();
        String sql = "SELECT * FROM passengers WHERE name LIKE ? OR email LIKE ? OR passport_no LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ searchPassengers: " + e.getMessage());
        }
        return list;
    }

    // ── Update ─────────────────────────────────────────────────────────────

    public boolean updatePassenger(Passenger p) {
        String sql = "UPDATE passengers SET name=?, email=?, phone=?, passport_no=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getEmail());
            ps.setString(3, p.getPhone());
            ps.setString(4, p.getPassportNo());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ updatePassenger: " + e.getMessage());
            return false;
        }
    }

    // ── Delete ─────────────────────────────────────────────────────────────

    public boolean deletePassenger(int id) {
        String sql = "DELETE FROM passengers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ deletePassenger: " + e.getMessage());
            return false;
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private Passenger mapRow(ResultSet rs) throws SQLException {
        Passenger p = new Passenger();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setEmail(rs.getString("email"));
        p.setPhone(rs.getString("phone"));
        p.setPassportNo(rs.getString("passport_no"));
        p.setCreatedAt(rs.getString("created_at"));
        return p;
    }
}
