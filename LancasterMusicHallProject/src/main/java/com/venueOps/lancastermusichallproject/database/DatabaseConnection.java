package com.venueOps.lancastermusichallproject.database;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.venueOps.lancastermusichallproject.operations.*;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Database Details
                String host = "sst-stuproj.city.ac.uk";
                String port = "3306";
                String dbName = "in2033t02";

                // Load credentials from .env
                Dotenv dotenv;
                try {
                    dotenv = Dotenv.load();
                } catch (Exception e) {
                    System.err.println("Failed to load .env file: " + e.getMessage());
                    return null;
                }
                String user = dotenv.get("DB_USERNAME");
                String password = dotenv.get("DB_PASSWORD");
                if (user == null || password == null) {
                    System.err.println("Database credentials not found in .env file");
                    return null;
                }

                // Connection
                String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to the database");
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            connection = null;
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fetches all events for specific time frame
    public static ArrayList<Event> getEventsForUsageChart(LocalDate start, LocalDate end) {
        ArrayList<Event> events = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return events;
            }
            // Fetch events
            String eventsQuery = "SELECT e.booking_id, e.event_id, e.name, e.type, e.start, e.end, e.max_discount, e.venue_id, v.name as venue_name, e.client_id, c.company_name AS client_name, sc.capacity " +
                    "FROM Events e " +
                    "JOIN Clients c ON e.client_id = c.client_id " +
                    "JOIN Venues v ON e.venue_id = v.venue_id " +
                    "LEFT JOIN SeatingConfigs sc ON e.seating_config_id = sc.seating_config_id " +
                    "WHERE e.start <= ? AND e.end >= ?";
            PreparedStatement eventsStmt = conn.prepareStatement(eventsQuery);
            eventsStmt.setString(1, end.plusDays(1).toString());
            eventsStmt.setString(2, start.minusDays(1).toString());
            ResultSet eventRs = eventsStmt.executeQuery();

            // Fetch daily ticket sales for each event
            String salesQuery = "SELECT event_date, tickets_sold FROM DailyTicketSales WHERE event_id = ?";
            PreparedStatement salesStmt = conn.prepareStatement(salesQuery);

            while (eventRs.next()) {
                int bookingID = eventRs.getInt("booking_id");
                int eventID = eventRs.getInt("event_id");
                String name = eventRs.getString("name");
                String type = eventRs.getString("type");
                String client = eventRs.getString("client_name");
                LocalDateTime startTimestamp = eventRs.getTimestamp("start").toLocalDateTime();
                LocalDateTime endTimestamp = eventRs.getTimestamp("end").toLocalDateTime();
                double max_discount = Double.parseDouble(eventRs.getString("max_discount"));
                int venueID = eventRs.getInt("venue_id");
                String venueName = eventRs.getString("venue_name");
                int capacity = eventRs.getInt("capacity");

                // Fetch daily ticket sales for this event
                Map<LocalDate, Integer> dailyTicketSales = new HashMap<>();
                salesStmt.setInt(1, eventID);
                ResultSet salesRs = salesStmt.executeQuery();
                while (salesRs.next()) {
                    LocalDate eventDate = salesRs.getDate("event_date").toLocalDate();
                    int ticketsSold = salesRs.getInt("tickets_sold");
                    dailyTicketSales.put(eventDate, ticketsSold);
                }
                salesRs.close();

                // Minimal seating config object just for capacity
                SeatingConfig seatingConfig = new SeatingConfig(0, capacity, null, venueName, null); // Adjust constructor as needed

                Event event = new Event(bookingID, eventID, name, type, client, startTimestamp, endTimestamp, BigDecimal.ZERO, BigDecimal.ZERO, max_discount, venueID, venueName, dailyTicketSales, seatingConfig);
                events.add(event);
            }

            eventRs.close();
            eventsStmt.close();
            salesStmt.close();
        } catch (SQLException e) {
            System.out.println("Failed to fetch events for Usage Chart from database" + e.getMessage());
            return events;
        }
        return events;
    }

    // Fetches all events for specific day
    public static ArrayList<Event> getEventsForDailySheet(LocalDateTime date) {
        ArrayList<Event> events = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return events;
            }
            String eventQuery = "SELECT e.booking_id, e.event_id, e.name, e.type, e.start, e.end, e.max_discount, e.venue_id, " +
                    "v.name as venue_name, e.client_id, c.company_name AS client_name, sc.seating_config_id, sc.layout " +
                    "FROM Events e " +
                    "JOIN Clients c ON e.client_id = c.client_id " +
                    "JOIN Venues v ON e.venue_id = v.venue_id " +
                    "LEFT JOIN SeatingConfigs sc ON e.seating_config_id = sc.seating_config_id " +
                    "WHERE DATE(e.start) <= ? AND ? <= DATE(e.end)";
            PreparedStatement eventStmt = conn.prepareStatement(eventQuery);
            eventStmt.setString(1, date.toLocalDate().toString()); // Set day for query to given day
            eventStmt.setString(2, date.toLocalDate().toString());
            ResultSet eventRs = eventStmt.executeQuery();

            // Create event objects and add to array
            while (eventRs.next()) {
                int bookingID = eventRs.getInt("booking_id");
                int eventID = eventRs.getInt("event_id");
                String name = eventRs.getString("name");
                String type = eventRs.getString("type");
                String client = eventRs.getString("client_name");
                LocalDateTime start = eventRs.getTimestamp("start").toLocalDateTime();
                LocalDateTime end = eventRs.getTimestamp("end").toLocalDateTime();
                double max_discount = Double.parseDouble(eventRs.getString("max_discount"));
                int venueID = eventRs.getInt("venue_id");
                String venueName = eventRs.getString("venue_name");
                int seatingConfigID = eventRs.getInt("seating_config_id");
                String layout = eventRs.getString("layout");

                // Create minimal seatingConfig object with only layout
                SeatingConfig seatingConfig = (layout != null)
                        ? new SeatingConfig(seatingConfigID, 0, layout, venueName, null)
                        : null;

                Event event = new Event(bookingID, eventID, name, type, client, start, end, BigDecimal.ZERO, BigDecimal.ZERO,
                        max_discount, venueID, venueName, null, seatingConfig);
                events.add(event);
            }

            eventRs.close();
            eventStmt.close();
        } catch (SQLException e) {
            System.out.println("Failed to fetch events for Daily Sheet from database" + e.getMessage());
            return events;
        }
        return events;
    }

    // Fetches all company names
    public static ObservableList<String> getCompanyNames() {
        ObservableList<String> companies = FXCollections.observableArrayList();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return companies;
            }
            String query = "SELECT company_name FROM Clients";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                companies.add(rs.getString("company_name"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch companies for Client details from database" + e.getMessage());
            return companies;
        }
        return companies;
    }

    public static Client getClientDetails(String companyName) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return null;
            }
            String query = "SELECT c.client_id, c.company_name, c.contact_first_name, c.contact_last_name, c.phone_number, c.email, b.address, b.city, b.postcode " +
                    "FROM Clients c " +
                    "JOIN Billing_Info b ON c.client_id = b.client_id " +
                    "WHERE c.company_name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, companyName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Client(
                        rs.getInt("client_id"),
                        rs.getString("company_name"),
                        rs.getString("contact_first_name"),
                        rs.getString("contact_last_name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("postcode")
                );
            }
        } catch  (SQLException e) {
            System.out.println("Failed to fetch client details for " + companyName + " from database" + e.getMessage());
            return null;
        }
        return null;
    }

    public static void saveBooking(Booking booking) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return;
            }
            conn.setAutoCommit(false);

            // Upsert Client
            ClientInfo clientInfo = upsertClient(conn, booking.getClient());

            // Insert Contract
            int contractID = insertContract(conn, clientInfo.getClientID(), booking.getTotalPrice(), booking.getSignedDate());

            // Insert Booking
            String insertBookingSql = "INSERT INTO Bookings (contract_id, start_date, end_date, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertBookingSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, contractID);
                stmt.setDate(2, Date.valueOf(booking.getStartDate()));
                stmt.setDate(3, Date.valueOf(booking.getEndDate()));
                stmt.setString(4, booking.getStatus());
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int bookingId = rs.getInt(1);
                    booking.setBookingID(bookingId); // Update Booking object with database assigned ID
                } else {
                    throw new SQLException("Failed to retrieve booking_id");
                }
            }

            // Insert Events
            insertEvents(conn, booking.getEvents(), clientInfo);

            conn.commit();
        } catch (SQLException e) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static ClientInfo upsertClient(Connection conn, Client client) throws SQLException {
        String checkSql = "SELECT client_id FROM Clients WHERE company_name = ? AND email = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, client.getCompanyName());
            checkStmt.setString(2, client.getEmail());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int clientID = rs.getInt("client_id");
                updateClient(conn, clientID, client);
                return new ClientInfo(clientID, client.getCompanyName());
            } else {
                return insertClient(conn, client);
            }
        }
    }

    private static ClientInfo insertClient(Connection conn, Client client) throws SQLException {
        String insertSql = "INSERT INTO Clients (company_name, contact_first_name, contact_last_name, phone_number, email) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, client.getCompanyName());
            stmt.setString(2, client.getContactFirstName());
            stmt.setString(3, client.getContactLastName());
            stmt.setString(4, client.getPhone());
            stmt.setString(5, client.getEmail());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return new ClientInfo(rs.getInt(1), client.getCompanyName());
            throw new SQLException("Failed to retrieve client info");
        }
    }

    private static void updateClient(Connection conn, int clientID, Client client) throws SQLException {
        String updateSql = "UPDATE Clients SET contact_first_name = ?, contact_last_name = ?, phone_number = ?, email = ? " +
                "WHERE client_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, client.getContactFirstName());
            stmt.setString(2, client.getContactLastName());
            stmt.setString(3, client.getPhone());
            stmt.setString(4, client.getEmail());
            stmt.setInt(5, clientID);
            stmt.executeUpdate();
        }
    }

    private static int insertContract(Connection conn, int clientID, BigDecimal totalPrice, LocalDate signedDate) throws SQLException {
        String insertSql = "INSERT INTO Contracts (client_id, signed_date, total_price, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, clientID);
            stmt.setDate(2, Date.valueOf(signedDate));
            stmt.setBigDecimal(3, totalPrice);
            stmt.setString(4, "Pending");
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Failed to retrieve contract_id");
        }
    }

    private static void insertEvents(Connection conn, List<IEvent> events, ClientInfo clientInfo) throws SQLException {
        String insertSql = "INSERT INTO Events (booking_id, event_id, name, type, host, start, end, price, ticket_price, max_discount, venue_id, client_id, seating_config_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            int eventID = 1;
            for (IEvent event : events) {
                try {
                    int seatingConfigID = insertSeatingConfig(conn, event.getSeatingConfig());
                    event.getSeatingConfig().setSeatingConfigID(seatingConfigID); // Update Event with Database assigned Seating Config

                    stmt.setInt(1, event.getBookingID());
                    stmt.setInt(2, eventID++);
                    stmt.setString(3, event.getEventName());
                    stmt.setString(4, event.getEventType());
                    stmt.setString(5, clientInfo.getCompanyName());
                    stmt.setTimestamp(6, Timestamp.valueOf(event.getEventStart()));
                    stmt.setTimestamp(7, Timestamp.valueOf(event.getEventEnd()));
                    stmt.setBigDecimal(8, event.getEventPrice());
                    stmt.setBigDecimal(9, event.getTicketPrice());
                    stmt.setDouble(10, event.getMaxDiscount());
                    stmt.setInt(11, event.getVenueID());
                    stmt.setInt(12, clientInfo.getClientID());
                    stmt.setInt(13, seatingConfigID);
                    stmt.addBatch();
                } catch (SQLException e) {
                    System.err.println("Error inserting event " + event.getEventName() + ": " + e.getMessage());
                    throw e;
                }
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Failed to insert events: " + e.getMessage());
            throw e;
        }
    }

    private static int insertSeatingConfig(Connection conn, SeatingConfig seatingConfig) throws SQLException {
        String insertSql = "INSERT INTO SeatingConfigs (capacity, layout) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, seatingConfig.getCapacity());
            stmt.setString(2, seatingConfig.getLayout());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int configID = rs.getInt(1);
                insertRestrictedViews(conn, configID, seatingConfig.getRestrictedViews());
                return configID;
            }
            throw new SQLException("Failed to retrieve seating_config_id");
        } catch (SQLException e) {
            System.err.println("Error inserting Seating Config: " + e.getMessage());
            throw e;
        }
    }

    private static void insertRestrictedViews(Connection conn, int seatingConfigId, ObservableList<String> restrictedViews) throws SQLException {
        if (restrictedViews == null || restrictedViews.isEmpty()) return;
        String insertSql = "INSERT INTO RestrictedViews (seating_config_id, seat_number) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (String seat : restrictedViews) {
                stmt.setInt(1, seatingConfigId);
                stmt.setString(2, seat);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error inserting Restricted Views: " + e.getMessage());
            throw e;
        }
    }

    // Helper class to pass Client info to be stored in the Event table
    public static class ClientInfo {
        private int clientID;
        private String companyName;

        public ClientInfo(int clientID, String companyName) {
            this.clientID = clientID;
            this.companyName = companyName;
        }

        public int getClientID() { return clientID; }
        public String getCompanyName() { return companyName; }
    }
}
