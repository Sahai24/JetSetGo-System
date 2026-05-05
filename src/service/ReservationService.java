package service;

import dao.*;
import model.*;

import java.util.List;
import java.util.UUID;

/**
 * Business logic layer — orchestrates DAO calls for reservations.
 */
public class ReservationService {

    private final FlightDAO      flightDAO      = new FlightDAO();
    private final PassengerDAO   passengerDAO   = new PassengerDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    // ── Booking ────────────────────────────────────────────────────────────

    /**
     * Books a flight for a passenger.
     * Returns the new Reservation on success, null on failure.
     */
    public Reservation bookFlight(int passengerId, int flightId, String seatNumber) {
        // 1. Validate passenger
        Passenger passenger = passengerDAO.getPassengerById(passengerId);
        if (passenger == null) {
            System.err.println("❌ Passenger not found: " + passengerId);
            return null;
        }

        // 2. Validate flight
        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            System.err.println("❌ Flight not found: " + flightId);
            return null;
        }

        // 3. Check availability
        if (flight.getAvailableSeats() <= 0) {
            System.err.println("❌ No seats available on flight: " + flight.getFlightNumber());
            return null;
        }

        // 4. Check seat not already taken
        List<String> booked = reservationDAO.getBookedSeats(flightId);
        if (booked.contains(seatNumber.toUpperCase())) {
            System.err.println("❌ Seat " + seatNumber + " is already booked.");
            return null;
        }

        // 5. Check passenger doesn't already have this flight
        List<Reservation> existing = reservationDAO.getReservationsByPassenger(passengerId);
        for (Reservation r : existing) {
            if (r.getFlightId() == flightId && "CONFIRMED".equals(r.getStatus())) {
                System.err.println("❌ Passenger already has a booking on this flight.");
                return null;
            }
        }

        // 6. Build reservation
        Reservation res = new Reservation();
        res.setBookingRef(generateBookingRef());
        res.setPassengerId(passengerId);
        res.setFlightId(flightId);
        res.setSeatNumber(seatNumber.toUpperCase());
        res.setStatus("CONFIRMED");
        res.setPaymentStatus("PAID");

        // 7. Persist & decrement seat (simple sequential; no TX needed for SQLite single-conn)
        boolean saved    = reservationDAO.addReservation(res);
        boolean seatDone = flightDAO.decrementSeat(flightId);

        if (saved && seatDone) {
            // Enrich with display info
            res.setPassengerName(passenger.getName());
            res.setFlightNumber(flight.getFlightNumber());
            res.setOrigin(flight.getOrigin());
            res.setDestination(flight.getDestination());
            res.setDepartureTime(flight.getDepartureTime());
            res.setPrice(flight.getPrice());
            return res;
        }

        System.err.println("❌ Booking failed at persistence step.");
        return null;
    }

    // ── Cancellation ───────────────────────────────────────────────────────

    public boolean cancelBooking(String bookingRef) {
        Reservation res = reservationDAO.getReservationByRef(bookingRef);
        if (res == null) {
            System.err.println("❌ Booking not found: " + bookingRef);
            return false;
        }
        if ("CANCELLED".equals(res.getStatus())) {
            System.err.println("⚠️  Booking already cancelled.");
            return false;
        }

        boolean cancelled = reservationDAO.cancelReservation(bookingRef);
        if (cancelled) {
            flightDAO.incrementSeat(res.getFlightId());
            System.out.println("✅ Booking " + bookingRef + " cancelled. Seat released.");
            return true;
        }
        return false;
    }

    // ── Queries ────────────────────────────────────────────────────────────

    public List<Flight> searchAvailableFlights(String origin, String destination) {
        return flightDAO.searchFlights(origin, destination);
    }

    public List<Reservation> getPassengerBookings(int passengerId) {
        return reservationDAO.getReservationsByPassenger(passengerId);
    }

    public Reservation getBookingByRef(String ref) {
        return reservationDAO.getReservationByRef(ref);
    }

    public List<Reservation> getAllBookings() {
        return reservationDAO.getAllReservations();
    }

    public List<String> getBookedSeats(int flightId) {
        return reservationDAO.getBookedSeats(flightId);
    }

    // ── Utilities ──────────────────────────────────────────────────────────

    private String generateBookingRef() {
        return "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
