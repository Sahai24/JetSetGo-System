import ui.ConsoleUI;
import util.DBConnection;
import util.DBInitializer;

/**
 * ✈  AirBook — Airline Reservation System
 * Entry point: initializes the DB, then launches the console UI.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize database schema & seed data
        DBInitializer.initialize();

        // Launch console UI
        ConsoleUI ui = new ConsoleUI();
        ui.start();

        // Clean up connection on exit
        DBConnection.closeConnection();
    }
}
