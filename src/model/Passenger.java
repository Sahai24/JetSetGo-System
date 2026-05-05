package model;

/**
 * Represents a passenger in the system.
 */
public class Passenger {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String passportNo;
    private String createdAt;

    public Passenger() {}

    public Passenger(String name, String email, String phone, String passportNo) {
        this.name       = name;
        this.email      = email;
        this.phone      = phone;
        this.passportNo = passportNo;
    }

    // Getters & Setters
    public int    getId()          { return id; }
    public void   setId(int id)    { this.id = id; }

    public String getName()              { return name; }
    public void   setName(String name)   { this.name = name; }

    public String getEmail()               { return email; }
    public void   setEmail(String email)   { this.email = email; }

    public String getPhone()               { return phone; }
    public void   setPhone(String phone)   { this.phone = phone; }

    public String getPassportNo()                    { return passportNo; }
    public void   setPassportNo(String passportNo)   { this.passportNo = passportNo; }

    public String getCreatedAt()                   { return createdAt; }
    public void   setCreatedAt(String createdAt)   { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("Passenger[id=%d, name=%s, email=%s, phone=%s, passport=%s]",
                id, name, email, phone, passportNo);
    }
}
