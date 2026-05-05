# ✈ AirBook — Airline Reservation System
### Java + JDBC + H2 Embedded Database

---

## 📋 Project Overview

A fully functional, console-based **Airline Reservation System** built with pure Java and JDBC. Uses the **H2 embedded database** — no external database server needed. All data persists automatically to a local file (`airline_reservation.mv.db`).

---

## 🏗 Architecture

```
AirlineReservationSystem/
├── src/
│   ├── Main.java                    ← Entry point
│   ├── model/
│   │   ├── Passenger.java           ← Passenger POJO
│   │   ├── Flight.java              ← Flight POJO
│   │   └── Reservation.java         ← Reservation POJO
│   ├── dao/
│   │   ├── PassengerDAO.java        ← JDBC CRUD for passengers
│   │   ├── FlightDAO.java           ← JDBC CRUD for flights
│   │   └── ReservationDAO.java      ← JDBC CRUD for reservations
│   ├── service/
│   │   └── ReservationService.java  ← Business logic layer
│   ├── util/
│   │   ├── DBConnection.java        ← JDBC connection manager
│   │   └── DBInitializer.java       ← Schema creation + data seeding
│   └── ui/
│       └── ConsoleUI.java           ← Menu-driven console interface
├── AirlineReservationSystem.jar     ← Self-contained executable JAR
└── README.md
```

### Design Pattern: **DAO (Data Access Object)**
- **Model layer** — POJOs representing database entities
- **DAO layer** — All JDBC operations (PreparedStatements, ResultSets)
- **Service layer** — Business rules, validation, orchestration
- **UI layer** — User interaction and display formatting

---

## 🗄 Database Schema (H2 / SQL)

### `passengers`
| Column      | Type         | Constraints         |
|-------------|--------------|---------------------|
| id          | INT AUTO_INCREMENT | PRIMARY KEY    |
| name        | VARCHAR(200) | NOT NULL            |
| email       | VARCHAR(200) | UNIQUE NOT NULL     |
| phone       | VARCHAR(20)  | NOT NULL            |
| passport_no | VARCHAR(50)  | UNIQUE NOT NULL     |
| created_at  | VARCHAR(30)  | DEFAULT NOW()       |

### `flights`
| Column          | Type         | Constraints     |
|-----------------|--------------|-----------------|
| id              | INT AUTO_INCREMENT | PRIMARY KEY |
| flight_number   | VARCHAR(20)  | UNIQUE NOT NULL |
| origin          | VARCHAR(100) | NOT NULL        |
| destination     | VARCHAR(100) | NOT NULL        |
| departure_time  | VARCHAR(30)  | NOT NULL        |
| arrival_time    | VARCHAR(30)  | NOT NULL        |
| total_seats     | INT          | NOT NULL        |
| available_seats | INT          | NOT NULL        |
| price           | DOUBLE       | NOT NULL        |
| status          | VARCHAR(20)  | DEFAULT 'SCHEDULED' |

### `reservations`
| Column         | Type        | Constraints              |
|----------------|-------------|--------------------------|
| id             | INT AUTO_INCREMENT | PRIMARY KEY       |
| booking_ref    | VARCHAR(20) | UNIQUE NOT NULL          |
| passenger_id   | INT         | FK → passengers(id)      |
| flight_id      | INT         | FK → flights(id)         |
| seat_number    | VARCHAR(10) | NOT NULL                 |
| booking_date   | VARCHAR(30) | DEFAULT NOW()            |
| status         | VARCHAR(20) | DEFAULT 'CONFIRMED'      |
| payment_status | VARCHAR(20) | DEFAULT 'PAID'           |

---

## ⚙ JDBC Features Demonstrated

| JDBC Feature              | Where Used                          |
|---------------------------|-------------------------------------|
| `DriverManager.getConnection()` | `DBConnection.java`           |
| `PreparedStatement`       | All DAO insert/update/delete queries |
| `Statement`               | Schema creation, seeding             |
| `ResultSet`               | Mapping rows to model objects        |
| `Statement.RETURN_GENERATED_KEYS` | Auto-ID retrieval after INSERT |
| `getGeneratedKeys()`      | Fetching generated primary keys      |
| `try-with-resources`      | Automatic resource management        |
| Foreign Key constraints   | reservations → passengers, flights   |
| Parameterized queries     | Preventing SQL injection             |

---

## 🚀 How to Run

### Prerequisites
- Java 17 or higher (`java -version`)

### Option 1: Run the pre-built JAR (easiest)
```bash
java -jar AirlineReservationSystem.jar
```

### Option 2: Compile from source
```bash
# Requires H2 jar on classpath
# On Ubuntu/Debian:
sudo apt-get install libh2-java

# Compile
javac -cp /usr/share/java/h2.jar -d out $(find src -name "*.java")

# Run
java -cp "out:/usr/share/java/h2.jar" Main
```

### Option 3: Use Maven
```bash
# pom.xml is included — just run:
mvn package
java -jar target/AirlineReservationSystem.jar
```

---

## 📱 Features

### 1. Passenger Management
- ✅ Register new passenger (name, email, phone, passport)
- ✅ View all passengers in a formatted table
- ✅ Search by name / email / passport number
- ✅ Update passenger details
- ✅ Delete passenger

### 2. Flight Management
- ✅ Add new flights with full schedule info
- ✅ View all flights with seat availability
- ✅ Search flights by origin → destination
- ✅ Update flight details / status
- ✅ Delete flights

### 3. Reservation Management
- ✅ Book a flight (with seat selection)
- ✅ Duplicate booking prevention (same passenger + flight)
- ✅ Seat conflict detection (no two passengers in same seat)
- ✅ Auto-generated booking reference (e.g. `BK4F2A91CE`)
- ✅ View all bookings
- ✅ View bookings by passenger
- ✅ Cancel booking (automatically restores seat availability)
- ✅ Booking confirmation receipt with full details

### 4. Reports
- ✅ Flight occupancy report (total / available / booked / fill %)
- ✅ Passenger manifest for a specific flight
- ✅ Available flight search by route

---

## 💡 Sample Data (Auto-seeded on first run)

| Flight | Route              | Departure        | Price  |
|--------|--------------------|------------------|--------|
| AI-101 | Delhi → Mumbai     | 2025-06-01 06:00 | ₹4,500 |
| AI-202 | Mumbai → Bangalore | 2025-06-01 09:00 | ₹3,800 |
| AI-303 | Bangalore → Chennai| 2025-06-01 11:00 | ₹2,500 |
| AI-404 | Chennai → Kolkata  | 2025-06-01 13:00 | ₹5,200 |
| AI-505 | Kolkata → Delhi    | 2025-06-01 16:00 | ₹4,800 |
| AI-606 | Delhi → Hyderabad  | 2025-06-02 07:00 | ₹4,200 |
| AI-707 | Hyderabad → Pune   | 2025-06-02 10:00 | ₹3,500 |
| AI-808 | Mumbai → Delhi     | 2025-06-02 14:00 | ₹4,600 |

---

## 🧾 Sample Session

```
✈  AirBook — Airline Reservation System
   Powered by Java + JDBC + H2 Database

MAIN MENU
  1. Passenger Management
  2. Flight Management
  3. Reservation Management
  4. Reports & Search
  0. Exit

Enter choice: 3

── RESERVATION MANAGEMENT ──
  1. Book a Flight
  ...

Enter Flight ID: 1
Passenger ID: 1
Seat Number: 12A

🎉 ═══════════ BOOKING CONFIRMED ═══════════
  ┌────────────────────────────────────────┐
  │ Booking Ref  : BK4F2A91CE             │
  │ Passenger    : Arnab Das              │
  │ Flight       : AI-101                 │
  │ Route        : Delhi → Mumbai         │
  │ Departure    : 2025-06-01 06:00       │
  │ Seat         : 12A                    │
  │ Amount       : ₹4500.00              │
  │ Status       : CONFIRMED              │
  └────────────────────────────────────────┘
```

---

*Built with ❤ using Java 17 + JDBC + H2 Embedded Database*
