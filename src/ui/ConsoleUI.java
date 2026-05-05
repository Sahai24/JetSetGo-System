package ui;

import dao.*;
import model.*;
import service.ReservationService;

import java.util.*;

/**
 * Console-based UI for the Airline Reservation System.
 * Provides a full menu-driven interface using Scanner.
 */
public class ConsoleUI {

    private static final Scanner sc = new Scanner(System.in);

    private final PassengerDAO      passengerDAO   = new PassengerDAO();
    private final FlightDAO         flightDAO      = new FlightDAO();
    private final ReservationDAO    reservationDAO = new ReservationDAO();
    private final ReservationService service       = new ReservationService();

    // ── Entry Point ────────────────────────────────────────────────────────

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1  -> passengerMenu();
                case 2  -> flightMenu();
                case 3  -> reservationMenu();
                case 4  -> reportsMenu();
                case 0  -> { running = false; System.out.println("\n👋 Thank you for using AirBook. Goodbye!\n"); }
                default -> System.out.println("⚠️  Invalid choice. Try again.");
            }
        }
    }

    // ── Menus ──────────────────────────────────────────────────────────────

    private void printMainMenu() {
        System.out.println("""
            
            ╔══════════════════════════════╗
            ║       MAIN MENU              ║
            ╠══════════════════════════════╣
            ║  1. Passenger Management     ║
            ║  2. Flight Management        ║
            ║  3. Reservation Management   ║
            ║  4. Reports & Search         ║
            ║  0. Exit                     ║
            ╚══════════════════════════════╝""");
    }

    // ─── Passenger ────────────────────────────────────────────────────────

    private void passengerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("""
                
                ── PASSENGER MANAGEMENT ──────────
                  1. Register New Passenger
                  2. View All Passengers
                  3. Search Passenger
                  4. Update Passenger
                  5. Delete Passenger
                  0. Back
                ──────────────────────────────────""");
            switch (readInt("Choice: ")) {
                case 1 -> registerPassenger();
                case 2 -> listAllPassengers();
                case 3 -> searchPassenger();
                case 4 -> updatePassenger();
                case 5 -> deletePassenger();
                case 0 -> back = true;
                default -> System.out.println("⚠️  Invalid.");
            }
        }
    }

    private void registerPassenger() {
        System.out.println("\n── Register New Passenger ────────");
        String name       = readString("Full Name      : ");
        String email      = readString("Email          : ");
        String phone      = readString("Phone          : ");
        String passport   = readString("Passport No.   : ");

        Passenger p = new Passenger(name, email, phone, passport);
        if (passengerDAO.addPassenger(p)) {
            System.out.printf("✅ Passenger registered! ID: %d%n", p.getId());
        } else {
            System.out.println("❌ Registration failed. Email/Passport may already exist.");
        }
    }

    private void listAllPassengers() {
        List<Passenger> list = passengerDAO.getAllPassengers();
        if (list.isEmpty()) { System.out.println("  No passengers registered."); return; }
        printLine();
        System.out.printf("%-5s %-20s %-25s %-15s %-12s%n",
                "ID", "Name", "Email", "Phone", "Passport");
        printLine();
        for (Passenger p : list) {
            System.out.printf("%-5d %-20s %-25s %-15s %-12s%n",
                    p.getId(), p.getName(), p.getEmail(), p.getPhone(), p.getPassportNo());
        }
        printLine();
    }

    private void searchPassenger() {
        String kw = readString("Search (name/email/passport): ");
        List<Passenger> list = passengerDAO.searchPassengers(kw);
        if (list.isEmpty()) { System.out.println("  No results."); return; }
        for (Passenger p : list)
            System.out.printf("  [%d] %s | %s | %s%n", p.getId(), p.getName(), p.getEmail(), p.getPassportNo());
    }

    private void updatePassenger() {
        int id = readInt("Passenger ID to update: ");
        Passenger p = passengerDAO.getPassengerById(id);
        if (p == null) { System.out.println("❌ Not found."); return; }

        System.out.println("  (Press Enter to keep current value)");
        String name  = readStringOpt("Name  [" + p.getName()  + "]: ");
        String email = readStringOpt("Email [" + p.getEmail() + "]: ");
        String phone = readStringOpt("Phone [" + p.getPhone() + "]: ");

        if (!name.isEmpty())  p.setName(name);
        if (!email.isEmpty()) p.setEmail(email);
        if (!phone.isEmpty()) p.setPhone(phone);

        System.out.println(passengerDAO.updatePassenger(p) ? "✅ Updated." : "❌ Update failed.");
    }

    private void deletePassenger() {
        int id = readInt("Passenger ID to delete: ");
        System.out.print("⚠️  Are you sure? (yes/no): ");
        if ("yes".equalsIgnoreCase(sc.nextLine().trim())) {
            System.out.println(passengerDAO.deletePassenger(id) ? "✅ Deleted." : "❌ Delete failed.");
        }
    }

    // ─── Flight ───────────────────────────────────────────────────────────

    private void flightMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("""
                
                ── FLIGHT MANAGEMENT ─────────────
                  1. Add New Flight
                  2. View All Flights
                  3. Search Flights (Origin→Dest)
                  4. Update Flight
                  5. Delete Flight
                  0. Back
                ──────────────────────────────────""");
            switch (readInt("Choice: ")) {
                case 1 -> addFlight();
                case 2 -> listAllFlights();
                case 3 -> searchFlights();
                case 4 -> updateFlight();
                case 5 -> deleteFlight();
                case 0 -> back = true;
                default -> System.out.println("⚠️  Invalid.");
            }
        }
    }

    private void addFlight() {
        System.out.println("\n── Add New Flight ────────────────");
        String number  = readString("Flight Number (e.g. AI-999): ");
        String origin  = readString("Origin City              : ");
        String dest    = readString("Destination City         : ");
        String dep     = readString("Departure (YYYY-MM-DD HH:MM): ");
        String arr     = readString("Arrival   (YYYY-MM-DD HH:MM): ");
        int    seats   = readInt("Total Seats              : ");
        double price   = readDouble("Price (₹)                : ");

        Flight f = new Flight(number, origin, dest, dep, arr, seats, seats, price);
        if (flightDAO.addFlight(f)) {
            System.out.printf("✅ Flight added! ID: %d%n", f.getId());
        } else {
            System.out.println("❌ Failed. Flight number may already exist.");
        }
    }

    private void listAllFlights() {
        List<Flight> list = flightDAO.getAllFlights();
        if (list.isEmpty()) { System.out.println("  No flights."); return; }
        printFlightTable(list);
    }

    private void searchFlights() {
        String origin = readString("Origin      : ");
        String dest   = readString("Destination : ");
        List<Flight> list = service.searchAvailableFlights(origin, dest);
        if (list.isEmpty()) {
            System.out.println("  No available flights found for that route.");
            return;
        }
        printFlightTable(list);
    }

    private void updateFlight() {
        int id = readInt("Flight ID to update: ");
        Flight f = flightDAO.getFlightById(id);
        if (f == null) { System.out.println("❌ Not found."); return; }

        System.out.println("  (Press Enter to keep current)");
        String dep   = readStringOpt("Departure [" + f.getDepartureTime() + "]: ");
        String arr   = readStringOpt("Arrival   [" + f.getArrivalTime()   + "]: ");
        String price = readStringOpt("Price     [" + f.getPrice()          + "]: ");
        String stat  = readStringOpt("Status    [" + f.getStatus()          + "]: ");

        if (!dep.isEmpty())   f.setDepartureTime(dep);
        if (!arr.isEmpty())   f.setArrivalTime(arr);
        if (!price.isEmpty()) f.setPrice(Double.parseDouble(price));
        if (!stat.isEmpty())  f.setStatus(stat.toUpperCase());

        System.out.println(flightDAO.updateFlight(f) ? "✅ Flight updated." : "❌ Update failed.");
    }

    private void deleteFlight() {
        int id = readInt("Flight ID to delete: ");
        System.out.print("⚠️  Are you sure? (yes/no): ");
        if ("yes".equalsIgnoreCase(sc.nextLine().trim())) {
            System.out.println(flightDAO.deleteFlight(id) ? "✅ Deleted." : "❌ Failed.");
        }
    }

    // ─── Reservation ──────────────────────────────────────────────────────

    private void reservationMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("""
                
                ── RESERVATION MANAGEMENT ────────
                  1. Book a Flight
                  2. View All Bookings
                  3. My Bookings (by Passenger ID)
                  4. Cancel Booking
                  5. Booking Details (by Ref)
                  0. Back
                ──────────────────────────────────""");
            switch (readInt("Choice: ")) {
                case 1 -> bookFlight();
                case 2 -> listAllBookings();
                case 3 -> myBookings();
                case 4 -> cancelBooking();
                case 5 -> bookingDetails();
                case 0 -> back = true;
                default -> System.out.println("⚠️  Invalid.");
            }
        }
    }

    private void bookFlight() {
        System.out.println("\n── Book a Flight ─────────────────");

        // Show available flights
        List<Flight> flights = flightDAO.getAvailableFlights();
        if (flights.isEmpty()) { System.out.println("  No available flights."); return; }
        printFlightTable(flights);

        int flightId = readInt("Enter Flight ID: ");
        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) { System.out.println("❌ Flight not found."); return; }

        // Show booked seats
        List<String> bookedSeats = service.getBookedSeats(flightId);
        if (!bookedSeats.isEmpty()) {
            System.out.println("  Already booked seats: " + bookedSeats);
        }

        int passId = readInt("Passenger ID     : ");
        String seat = readString("Seat Number (e.g. 12A): ");

        Reservation res = service.bookFlight(passId, flightId, seat);
        if (res != null) {
            System.out.println("\n🎉 ═══════════ BOOKING CONFIRMED ═══════════");
            printBookingReceipt(res);
        }
    }

    private void listAllBookings() {
        List<Reservation> list = service.getAllBookings();
        if (list.isEmpty()) { System.out.println("  No bookings found."); return; }
        printBookingTable(list);
    }

    private void myBookings() {
        int id = readInt("Passenger ID: ");
        List<Reservation> list = service.getPassengerBookings(id);
        if (list.isEmpty()) { System.out.println("  No bookings for this passenger."); return; }
        printBookingTable(list);
    }

    private void cancelBooking() {
        String ref = readString("Booking Reference: ");
        service.cancelBooking(ref);
    }

    private void bookingDetails() {
        String ref = readString("Booking Reference: ");
        Reservation r = service.getBookingByRef(ref);
        if (r == null) { System.out.println("❌ Not found."); return; }
        printBookingReceipt(r);
    }

    // ─── Reports ──────────────────────────────────────────────────────────

    private void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("""
                
                ── REPORTS & SEARCH ──────────────
                  1. Flight Occupancy Report
                  2. Passengers on a Flight
                  3. Search Available Flights
                  0. Back
                ──────────────────────────────────""");
            switch (readInt("Choice: ")) {
                case 1 -> occupancyReport();
                case 2 -> passengersOnFlight();
                case 3 -> searchFlights();
                case 0 -> back = true;
                default -> System.out.println("⚠️  Invalid.");
            }
        }
    }

    private void occupancyReport() {
        List<Flight> flights = flightDAO.getAllFlights();
        printLine();
        System.out.printf("%-10s %-12s %-14s %-8s %-8s %-8s %-6s%n",
                "ID", "Flight", "Route", "Total", "Avail", "Booked", "Fill%");
        printLine();
        for (Flight f : flights) {
            int booked = f.getTotalSeats() - f.getAvailableSeats();
            double pct = f.getTotalSeats() > 0
                    ? (booked * 100.0 / f.getTotalSeats()) : 0;
            System.out.printf("%-10d %-12s %-14s %-8d %-8d %-8d %.1f%%%n",
                    f.getId(), f.getFlightNumber(),
                    f.getOrigin() + "→" + f.getDestination(),
                    f.getTotalSeats(), f.getAvailableSeats(), booked, pct);
        }
        printLine();
    }

    private void passengersOnFlight() {
        int id = readInt("Flight ID: ");
        Flight f = flightDAO.getFlightById(id);
        if (f == null) { System.out.println("❌ Flight not found."); return; }

        List<Reservation> list = reservationDAO.getReservationsByFlight(id);
        System.out.printf("%nPassengers on %s (%s → %s):%n",
                f.getFlightNumber(), f.getOrigin(), f.getDestination());
        if (list.isEmpty()) { System.out.println("  No confirmed bookings."); return; }

        printLine();
        System.out.printf("%-12s %-20s %-6s %-10s%n", "Ref", "Passenger", "Seat", "Status");
        printLine();
        for (Reservation r : list) {
            System.out.printf("%-12s %-20s %-6s %-10s%n",
                    r.getBookingRef(), r.getPassengerName(), r.getSeatNumber(), r.getStatus());
        }
        printLine();
    }

    // ── Display Helpers ────────────────────────────────────────────────────

    private void printFlightTable(List<Flight> list) {
        printLine();
        System.out.printf("%-5s %-10s %-12s %-12s %-18s %-18s %-6s %-8s%n",
                "ID", "Flight", "From", "To", "Departure", "Arrival", "Seats", "Price");
        printLine();
        for (Flight f : list) {
            System.out.printf("%-5d %-10s %-12s %-12s %-18s %-18s %-6d ₹%-7.0f%n",
                    f.getId(), f.getFlightNumber(), f.getOrigin(), f.getDestination(),
                    f.getDepartureTime(), f.getArrivalTime(), f.getAvailableSeats(), f.getPrice());
        }
        printLine();
    }

    private void printBookingTable(List<Reservation> list) {
        printLine();
        System.out.printf("%-12s %-18s %-10s %-12s %-12s %-5s %-10s%n",
                "Ref", "Passenger", "Flight", "From", "To", "Seat", "Status");
        printLine();
        for (Reservation r : list) {
            System.out.printf("%-12s %-18s %-10s %-12s %-12s %-5s %-10s%n",
                    r.getBookingRef(), r.getPassengerName(), r.getFlightNumber(),
                    r.getOrigin(), r.getDestination(), r.getSeatNumber(), r.getStatus());
        }
        printLine();
    }

    private void printBookingReceipt(Reservation r) {
        System.out.println("  ┌──────────────────────────────────────┐");
        System.out.printf ("  │ Booking Ref  : %-23s│%n", r.getBookingRef());
        System.out.printf ("  │ Passenger    : %-23s│%n", r.getPassengerName());
        System.out.printf ("  │ Flight       : %-23s│%n", r.getFlightNumber());
        System.out.printf ("  │ Route        : %-23s│%n", r.getOrigin() + " → " + r.getDestination());
        System.out.printf ("  │ Departure    : %-23s│%n", r.getDepartureTime());
        System.out.printf ("  │ Seat         : %-23s│%n", r.getSeatNumber());
        System.out.printf ("  │ Amount       : ₹%-22.2f│%n", r.getPrice());
        System.out.printf ("  │ Status       : %-23s│%n", r.getStatus());
        System.out.println("  └──────────────────────────────────────┘");
    }

    private void printBanner() {
        System.out.println("""
            
            ╔════════════════════════════════════════════════╗
            ║      ✈  AirBook — Airline Reservation System  ║
            ║         Powered by Java + JDBC + H2 Database        ║
            ╚════════════════════════════════════════════════╝
            """);
    }

    private void printLine() {
        System.out.println("─".repeat(90));
    }

    // ── Input Helpers ──────────────────────────────────────────────────────

    private String readString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private String readStringOpt(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number.");
            }
        }
    }
}
