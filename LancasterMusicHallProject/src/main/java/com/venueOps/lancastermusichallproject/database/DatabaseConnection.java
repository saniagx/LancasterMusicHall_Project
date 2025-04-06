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
            String eventsQuery = "SELECT e.booking_id, e.event_id, e.name, e.type, e.start, e.end, e.max_discount, e.venue_id, " +
                    "v.name as venue_name, e.client_id, c.company_name AS client_name, sc.capacity, b.status " +
                    "FROM Events e " +
                    "JOIN Clients c ON e.client_id = c.client_id " +
                    "JOIN Venues v ON e.venue_id = v.venue_id " +
                    "JOIN Bookings b ON e.booking_id = b.booking_id " +
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
                String status = eventRs.getString("status");

                if (!status.equals("Cancelled")) { // Only show events from bookings that aren't cancelled
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
                    SeatingConfig seatingConfig = new SeatingConfig(0, capacity, null, venueName, null);

                    Event event = new Event(bookingID, eventID, name, type, client, startTimestamp, endTimestamp, BigDecimal.ZERO, BigDecimal.ZERO, max_discount, venueID, venueName, dailyTicketSales, seatingConfig);
                    events.add(event);
                }
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
                    "v.name as venue_name, e.client_id, c.company_name AS client_name, sc.seating_config_id, sc.layout, b.status " +
                    "FROM Events e " +
                    "JOIN Clients c ON e.client_id = c.client_id " +
                    "JOIN Venues v ON e.venue_id = v.venue_id " +
                    "JOIN Bookings b ON e.booking_id = b.booking_id " +
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
                String status = eventRs.getString("status");

                if (!status.equals("Cancelled")) { // Only show events from bookings that aren't cancelled
                    // Create minimal seatingConfig object with only layout
                    SeatingConfig seatingConfig = (layout != null)
                            ? new SeatingConfig(seatingConfigID, 0, layout, venueName, null)
                            : null;

                    Event event = new Event(bookingID, eventID, name, type, client, start, end, BigDecimal.ZERO, BigDecimal.ZERO,
                            max_discount, venueID, venueName, null, seatingConfig);
                    events.add(event);
                }
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
        } catch (SQLException e) {
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
            String insertBookingSql = "INSERT INTO Bookings (contract_id, booking_name, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertBookingSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, contractID);
                stmt.setString(2, booking.getBookingName());
                stmt.setDate(3, Date.valueOf(booking.getStartDate()));
                stmt.setDate(4, Date.valueOf(booking.getEndDate()));
                stmt.setString(5, booking.getStatus());
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
            // Insert Invoices
            BigDecimal totalPrice = booking.getTotalPrice();

            insertInvoice(conn, booking.getBookingID(), totalPrice);
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

    private static void insertInvoice(Connection conn, int bookingId, BigDecimal totalPrice) throws SQLException {
        String sql = "INSERT INTO Invoices (booking_id, issue_date, due_date, total_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.setDate(2, Date.valueOf(LocalDate.now()));
            stmt.setDate(3, Date.valueOf(LocalDate.now().plusDays(14))); // e.g. 2-week due date
            stmt.setBigDecimal(4, totalPrice);
            stmt.executeUpdate();
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
            System.out.println("Inserted capacity: " + seatingConfig.getCapacity());
            System.out.println("Inserted layout: " + seatingConfig.getLayout());
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

    public static List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.booking_id, b.booking_name, cl.company_name, cl.contact_first_name, cl.contact_last_name, cl.email, cl.phone_number, ct.signed_date, b.start_date, b.end_date, b.status " +
                "FROM Bookings b " +
                "JOIN Contracts ct ON b.contract_id = ct.contract_id " +
                "JOIN Clients cl ON ct.client_id = cl.client_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return bookings;
            }
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            {
                LocalDate today = LocalDate.now();
                while (rs.next()) {
                    int bookingID = rs.getInt("booking_id");
                    String bookingName = rs.getString("booking_name");
                    String companyName = rs.getString("company_name");
                    String contact_FName = rs.getString("contact_first_name");
                    String contact_LName = rs.getString("contact_last_name");
                    String email = rs.getString("email");
                    String phoneNumber = rs.getString("phone_number");
                    LocalDate signedDate = rs.getDate("signed_date").toLocalDate();
                    LocalDate startDate = rs.getDate("start_date").toLocalDate();
                    LocalDate endDate = rs.getDate("end_date").toLocalDate();
                    String status = rs.getString("status");

                    List<IEvent> events = getEventsForBooking(conn, bookingID);
                    Client client = new Client(0, companyName, contact_FName, contact_LName, email, phoneNumber, null, null, null);

                    // Auto-update status to Completed if confirmed Booking is past end date
                    if (status.equals("Confirmed") && today.isAfter(endDate)) {
                        updateBookingStatus(bookingID, "Completed");
                        status = "Completed";
                    }

                    Booking booking = new Booking(bookingID, bookingName, events, client, signedDate, BigDecimal.ZERO, startDate, endDate, status);
                    bookings.add(booking);
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Failed to get bookings: " + e.getMessage());
        }
        return bookings;
    }

    private static List<IEvent> getEventsForBooking(Connection conn, int bookingID) throws SQLException {
        List<IEvent> events = new ArrayList<>();
        String eventsQuery = "SELECT e.name, e.type, e.start, e.end, e.ticket_price, e.max_discount, e.venue_id, v.name AS venue_name " +
                "FROM Events e " +
                "JOIN Venues v ON e.venue_id = v.venue_id " +
                "WHERE e.booking_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(eventsQuery)) {
            stmt.setInt(1, bookingID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();
                BigDecimal ticketPrice = rs.getBigDecimal("ticket_price");
                double maxDiscount = rs.getDouble("max_discount");
                int venueID = rs.getInt("venue_id");
                String venueName = rs.getString("venue_name");

                Event event = new Event(bookingID, 0, name, type, null, start, end,
                        BigDecimal.ZERO, ticketPrice, maxDiscount, venueID, venueName, null, null);
                events.add(event);
            }
        }
        return events;
    }

    public static List<InvoiceInfo> getInvoices() {
        List<InvoiceInfo> invoices = new ArrayList<>();

        String sql = """
        SELECT 
            i.invoice_id,
            i.booking_id,
            i.issue_date,
            i.due_date,
            i.total_price,
            GROUP_CONCAT(e.name SEPARATOR ', ') AS event_names,
            CONCAT(c.contact_first_name, ' ', c.contact_last_name) AS client_name
        FROM Invoices i
        JOIN Bookings b ON i.booking_id = b.booking_id
        JOIN Events e ON e.booking_id = b.booking_id
        JOIN Contracts ct ON b.contract_id = ct.contract_id
        JOIN Clients c ON ct.client_id = c.client_id
        GROUP BY i.invoice_id;
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                InvoiceInfo invoice = new InvoiceInfo(
                        rs.getInt("invoice_id"),
                        rs.getInt("booking_id"),
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getBigDecimal("total_price"),
                        rs.getString("event_names"),
                        rs.getString("client_name")
                );
                invoices.add(invoice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoices;
    }

    public static void updateBookingStatus(int bookingID, String newStatus) throws SQLException {
        String updateQuery = "UPDATE Bookings SET status = ? WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, bookingID);
            stmt.executeUpdate();
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

        public int getClientID() {
            return clientID;
        }

        public String getCompanyName() {
            return companyName;
        }
    }


    //get events for the invoice(and contracts) table
    //without date parameters

    public static ArrayList<Event> getEventsForInvoicesAndContracts() {
        ArrayList<Event> events = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return events;
            }
            String query = "SELECT e.booking_id, e.event_id, e.name, e.type, e.start, e.end, e.max_discount, e.venue_id, " +
                    "v.name as venue_name, e.client_id, c.company_name AS client_name, sc.seating_config_id, sc.layout, b.status " +
                    "FROM Events e " +
                    "JOIN Clients c ON e.client_id = c.client_id " +
                    "JOIN Venues v ON e.venue_id = v.venue_id " +
                    "JOIN Bookings b ON e.booking_id = b.booking_id " +
                    "LEFT JOIN SeatingConfigs sc ON e.seating_config_id = sc.seating_config_id " +
                    "WHERE DATE(e.start) <= ? AND ? <= DATE(e.end)";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Fetch daily ticket sales for each event
            String salesQuery = "SELECT event_date, tickets_sold FROM DailyTicketSales WHERE event_id = ?";
            PreparedStatement salesStmt = conn.prepareStatement(salesQuery);

            while (rs.next()) {
                int bookingID = rs.getInt("booking_id");
                int eventID = rs.getInt("event_id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String client = rs.getString("client_name");
                LocalDateTime startTimestamp = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime endTimestamp = rs.getTimestamp("end").toLocalDateTime();
                double max_discount = Double.parseDouble(rs.getString("max_discount"));
                int venueID = rs.getInt("venue_id");
                String venueName = rs.getString("venue_name");
                int capacity = rs.getInt("capacity");
                String status = rs.getString("status");

                if (!status.equals("Cancelled")) { // Only show events from bookings that aren't cancelled
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
                    SeatingConfig seatingConfig = new SeatingConfig(0, capacity, null, venueName, null);

                    Event event = new Event(bookingID, eventID, name, type, client, startTimestamp, endTimestamp, BigDecimal.ZERO, BigDecimal.ZERO, max_discount, venueID, venueName, dailyTicketSales, seatingConfig);
                    events.add(event);
                }
            }

            rs.close();
            stmt.close();
            salesStmt.close();
        } catch (SQLException e) {
            System.out.println("Failed to fetch events for Invoices from database" + e.getMessage());
            return events;
        }
        return events;
    }

    public static HashMap<String, String> getDiaryNotes(LocalDate timeframeStart, LocalDate timeframeEnd) {
        HashMap<String, String> diaryMap = new HashMap<>();
        Connection conn = DatabaseConnection.getConnection();
        try {
            if (conn == null) {
                return diaryMap;
            }
            String query = "SELECT noteDate, text " +
                    "FROM DiaryNotes " +
                    "WHERE noteDate >= ? AND noteDate <= ? ";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, timeframeStart.toString());
            stmt.setString(2, timeframeEnd.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Date noteDate = rs.getDate("noteDate");
                String text = rs.getString("text");

                diaryMap.put(noteDate.toString(), text);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Failed to fetch events for Invoices from database" + e.getMessage());
            return diaryMap;
        }
        return diaryMap;
    }

    public static void saveDiaryNote(DiaryNote diaryNote) {
        Connection conn = DatabaseConnection.getConnection();
        try {
            String checkSQL = "SELECT COUNT(*) FROM DiaryNotes WHERE noteDate = ?";
            PreparedStatement stmt = conn.prepareStatement(checkSQL);
            stmt.setDate(1, java.sql.Date.valueOf(diaryNote.getDate()));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                // Update existing note
                String updateSQL = "UPDATE DiaryNotes SET text = ? WHERE noteDate = ?";
                stmt = conn.prepareStatement(updateSQL);
                stmt.setString(1, diaryNote.getText());
                stmt.setDate(2, java.sql.Date.valueOf(diaryNote.getDate()));
                stmt.executeUpdate();
            } else {
                // Insert new note
                String insertSQL = "INSERT INTO DiaryNotes (noteDate, text) VALUES (?, ?)";
                stmt = conn.prepareStatement(insertSQL);
                stmt.setDate(1, java.sql.Date.valueOf(diaryNote.getDate()));
                stmt.setString(2, diaryNote.getText());
                stmt.executeUpdate();
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error saving diary note: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void deleteNote(DiaryNote diaryNote) {
        Connection conn = DatabaseConnection.getConnection();
        try {
            String deleteSQL = "DELETE FROM DiaryNotes WHERE noteDate = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteSQL);
            stmt.setDate(1, java.sql.Date.valueOf(diaryNote.getDate()));
            stmt.executeUpdate();

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error deleting diary note: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}



