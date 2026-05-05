package model;

/**
 * Represents a flight in the system.
 */
public class Flight {
    private int    id;
    private String flightNumber;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private int    totalSeats;
    private int    availableSeats;
    private double price;
    private String status;

    public Flight() {}

    public Flight(String flightNumber, String origin, String destination,
                  String departureTime, String arrivalTime,
                  int totalSeats, int availableSeats, double price) {
        this.flightNumber   = flightNumber;
        this.origin         = origin;
        this.destination    = destination;
        this.departureTime  = departureTime;
        this.arrivalTime    = arrivalTime;
        this.totalSeats     = totalSeats;
        this.availableSeats = availableSeats;
        this.price          = price;
        this.status         = "SCHEDULED";
    }

    // Getters & Setters
    public int    getId()          { return id; }
    public void   setId(int id)    { this.id = id; }

    public String getFlightNumber()                      { return flightNumber; }
    public void   setFlightNumber(String flightNumber)   { this.flightNumber = flightNumber; }

    public String getOrigin()                { return origin; }
    public void   setOrigin(String origin)   { this.origin = origin; }

    public String getDestination()                     { return destination; }
    public void   setDestination(String d)             { this.destination = d; }

    public String getDepartureTime()                         { return departureTime; }
    public void   setDepartureTime(String departureTime)     { this.departureTime = departureTime; }

    public String getArrivalTime()                       { return arrivalTime; }
    public void   setArrivalTime(String arrivalTime)     { this.arrivalTime = arrivalTime; }

    public int  getTotalSeats()                  { return totalSeats; }
    public void setTotalSeats(int totalSeats)    { this.totalSeats = totalSeats; }

    public int  getAvailableSeats()                      { return availableSeats; }
    public void setAvailableSeats(int availableSeats)    { this.availableSeats = availableSeats; }

    public double getPrice()               { return price; }
    public void   setPrice(double price)   { this.price = price; }

    public String getStatus()                { return status; }
    public void   setStatus(String status)   { this.status = status; }

    @Override
    public String toString() {
        return String.format(
            "Flight[%s | %s → %s | Dep: %s | Arr: %s | Seats: %d/%d | ₹%.2f | %s]",
            flightNumber, origin, destination,
            departureTime, arrivalTime,
            availableSeats, totalSeats,
            price, status);
    }
}
