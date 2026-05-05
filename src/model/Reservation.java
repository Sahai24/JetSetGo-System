package model;

/**
 * Represents a flight reservation/booking.
 */
public class Reservation {
    private int    id;
    private String bookingRef;
    private int    passengerId;
    private int    flightId;
    private String seatNumber;
    private String bookingDate;
    private String status;
    private String paymentStatus;

    // Joined fields for display
    private String passengerName;
    private String flightNumber;
    private String origin;
    private String destination;
    private String departureTime;
    private double price;

    public Reservation() {}

    // Getters & Setters
    public int    getId()          { return id; }
    public void   setId(int id)    { this.id = id; }

    public String getBookingRef()                    { return bookingRef; }
    public void   setBookingRef(String bookingRef)   { this.bookingRef = bookingRef; }

    public int  getPassengerId()                     { return passengerId; }
    public void setPassengerId(int passengerId)      { this.passengerId = passengerId; }

    public int  getFlightId()                  { return flightId; }
    public void setFlightId(int flightId)      { this.flightId = flightId; }

    public String getSeatNumber()                    { return seatNumber; }
    public void   setSeatNumber(String seatNumber)   { this.seatNumber = seatNumber; }

    public String getBookingDate()                       { return bookingDate; }
    public void   setBookingDate(String bookingDate)     { this.bookingDate = bookingDate; }

    public String getStatus()                { return status; }
    public void   setStatus(String status)   { this.status = status; }

    public String getPaymentStatus()                         { return paymentStatus; }
    public void   setPaymentStatus(String paymentStatus)     { this.paymentStatus = paymentStatus; }

    // Joined display fields
    public String getPassengerName()                     { return passengerName; }
    public void   setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getFlightNumber()                      { return flightNumber; }
    public void   setFlightNumber(String flightNumber)   { this.flightNumber = flightNumber; }

    public String getOrigin()                { return origin; }
    public void   setOrigin(String origin)   { this.origin = origin; }

    public String getDestination()                     { return destination; }
    public void   setDestination(String destination)   { this.destination = destination; }

    public String getDepartureTime()                         { return departureTime; }
    public void   setDepartureTime(String departureTime)     { this.departureTime = departureTime; }

    public double getPrice()               { return price; }
    public void   setPrice(double price)   { this.price = price; }

    @Override
    public String toString() {
        return String.format(
            "Reservation[Ref: %s | %s | Flight: %s (%s→%s) | Seat: %s | ₹%.2f | %s]",
            bookingRef, passengerName, flightNumber,
            origin, destination, seatNumber, price, status);
    }
}
