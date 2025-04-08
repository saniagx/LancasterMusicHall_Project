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
import javafx.scene.shape.Rectangle;

/**
 * This class connects the application to the database, handling the fetching, storing, deleting and updating of data
 * @author Neil Daya
 * @author Meer Ali
 * @author Sania Ghori
 * @version 6.0 April 7 2025
 */
public class DatabaseConnection {
    private static Connection connection;


    /**
     * Attempts to connect to the database using credentials from the .env
     * @return the connection variable used to perform database operations
     */
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

    /**
     * Closes the database connection
     */
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

    /**
     * Fetches all events within a given time frame. Used in the Usage Chart
     * @param start start of time frame
     * @param end end date of time frame
     * @return list of events occurring between the start and end dates to be displayed on the usage chart
     */
    public static ArrayList<Event> getEventsForUsageChart(LocalDate start, LocalDate end) {
        ArrayList<Event> events = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return events;
            }
            // Fetch events
            String eventsQuery = "SELECT e.booking_id, e.event_id, e.name, e.type, e.start, e.end, e.ticket_price, e.max_discount, e.venue_id, " +
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

            while (eventRs.next()) {
                int bookingID = eventRs.getInt("booking_id");
                int eventID = eventRs.getInt("event_id");
                String name = eventRs.getString("name");
                String type = eventRs.getString("type");
                String client = eventRs.getString("client_name");
                LocalDateTime startTimestamp = eventRs.getTimestamp("start").toLocalDateTime();
                LocalDateTime endTimestamp = eventRs.getTimestamp("end").toLocalDateTime();
                BigDecimal ticketPrice = new BigDecimal(eventRs.getString("ticket_price"));
                double max_discount = Double.parseDouble(eventRs.getString("max_discount"));
                int venueID = eventRs.getInt("venue_id");
                String venueName = eventRs.getString("venue_name");
                int capacity = eventRs.getInt("capacity");
                String status = eventRs.getString("status");

                if (!status.equals("Cancelled")) { // Only show events from bookings that aren't cancelled
                    // Fetch daily ticket sales for this event
                    Map<LocalDate, Integer> dailyTicketSales = new HashMap<>();
                    // Fetch daily ticket sales for each event
                    String salesQuery = "SELECT event_date, tickets_sold FROM DailyTicketSales " +
                            "WHERE booking_id = ? AND event_id = ?";
                    try (PreparedStatement salesStmt = conn.prepareStatement(salesQuery)) {
                        salesStmt.setInt(1, bookingID);
                        salesStmt.setInt(2, eventID);
                        ResultSet salesRs = salesStmt.executeQuery();
                        while (salesRs.next()) {
                            LocalDate eventDate = salesRs.getDate("event_date").toLocalDate();
                            int ticketsSold = salesRs.getInt("tickets_sold");
                            dailyTicketSales.put(eventDate, ticketsSold);
                        }
                    }

                    // Minimal seating config object just for capacity
                    SeatingConfig seatingConfig = new SeatingConfig(0, capacity, null, venueName, null);

                    Event event = new Event(bookingID, eventID, name, type, client, startTimestamp, endTimestamp, BigDecimal.ZERO,
                            ticketPrice, max_discount, venueID, venueName, dailyTicketSales, seatingConfig);
                    events.add(event);
                }
            }

            eventRs.close();
            eventsStmt.close();
        } catch (SQLException e) {
            System.out.println("Failed to fetch events for Usage Chart from database" + e.getMessage());
            return events;
        }
        return events;
    }

    /**
     * Fetches events for the daily sheet
     * @param date  the date the database fetches events for, by default set to today's date
     * @return  list of events occurring on the date to be displayed on the daily sheet
     */
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

    /**
     * Fetches all company names for the Client details tab in the New Booking screen
     * @return  list of company names to be displayed on the Client details tab
     */
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

    /**
     * Fetches the client details for a given company
     * @param companyName name of the company to get details for
     * @return Client object containing all the client's details
     */
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

    /**
     * Saves a booking to the database along with its Event information, Seating Configurations, Contract and Invoice
     * Rolls back if unsuccessful
     * @param booking the booking to be stored
     */
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
            // Insert Invoice
            insertInvoice(conn, booking.getBookingID(), booking.getTotalPrice());

            conn.commit();
        } catch (SQLException e) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Updates or inserts the Client table in the database depending on if their details already existed or not
     * @param conn connection variable for the database
     * @param client client object to be updated or inserted
     * @return ClientInfo object containing clientID and companyName to be used when saving the booking
     * @throws SQLException
     */
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

    /**
     * Inserts the given Client into the database
     * @param conn connection variable for the database
     * @param client client object to be inserted
     * @return ClientInfo object containing clientID and companyName to be used when saving the booking
     * @throws SQLException
     */
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

    /**
     * Updates the details of the given Client to its new details
     * @param conn connection variable for the database
     * @param clientID unique identifier for the client
     * @param client client object containing the new details
     * @throws SQLException
     */
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

    /**
     * Inserts a booking's contract into the database
     * @param conn connection variable for the database
     * @param clientID unique identifier for the client
     * @param totalPrice total price of the booking
     * @param signedDate date the contract was made
     * @return the contract id to be saved with the booking
     * @throws SQLException
     */
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

    /**
     * Inserts an invoice into the database into the database
     * @param conn connection variable for the database
     * @param bookingId unique identifier for the booking
     * @param totalPrice total price of all the events in the booking
     * @throws SQLException
     */
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

    /**
     * Inserts the given booking's events into the database
     * @param conn connection variable for the database
     * @param events list of events to be stored
     * @param clientInfo client who hired the venue
     * @throws SQLException
     */
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

    /**
     * Inserts an event's seating configuration into the database
     * @param conn connection variable for the database
     * @param seatingConfig seating configuration object containing seating information
     * @return seating configuration unique identifier to be used to link to the event
     * @throws SQLException
     */
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

    /**
     * Inserts all restricted seats belonging to a given seating configuration into the database
     * @param conn connection variable for the database
     * @param seatingConfigId unique identifier to link a restricted seat to the seating configuration it belongs to
     * @param restrictedViews list of seats with restricted views
     * @throws SQLException
     */
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

    /**
     * Fetches all bookings
     * @return list of all bookings to be used in the calendar and bookings overview
     */
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

    /**
     * Fetches all the events that belong to a booking for the view events tab in bookings overview
     * @param conn connection variable for the database
     * @param bookingID unique identifier for a booking
     * @return list of events belonging to a booking
     * @throws SQLException
     */
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

    /**
     * Fetches all venues for a given invoice for the invoice page
     * @param bookingId unique identifier for the given invoice
     * @return list of all venues for the given invoice
     */
    public static List<VenueTable> getVenuesForInvoice(int bookingId) {
        List<VenueTable> venueList = new ArrayList<>();
        String sql = """
        SELECT v.name AS venue_name, e.price AS venue_price
        FROM Events e
        JOIN Venues v ON e.venue_id = v.venue_id
        WHERE e.booking_id = ?
    """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String venueName = rs.getString("venue_name");
                BigDecimal price = rs.getBigDecimal("venue_price");
                venueList.add(new VenueTable(venueName, price));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching venues for invoice: " + e.getMessage());
        }

        return venueList;
    }

    /**
     * Fetches all invoices to be displayed on the invoices screen
     * @return list of invoices
     */
    public static List<InvoiceInfo> getInvoices() {
        List<InvoiceInfo> invoices = new ArrayList<>();

        String sql = """
        SELECT 
            i.invoice_id,
            i.booking_id,
            i.issue_date,
            i.due_date,
            i.total_price,
            c.email,
            bi.address,
            bi.city,
            bi.postcode,
            GROUP_CONCAT(e.name SEPARATOR ', ') AS event_names,
            CONCAT(c.contact_first_name, ' ', c.contact_last_name) AS client_name
        FROM Invoices i
        JOIN Bookings b ON i.booking_id = b.booking_id
        JOIN Events e ON e.booking_id = b.booking_id
        JOIN Contracts ct ON b.contract_id = ct.contract_id
        JOIN Clients c ON ct.client_id = c.client_id
        JOIN Billing_Info bi ON bi.client_id = c.client_id
        GROUP BY i.invoice_id;
        """;

        try {
            Connection conn = getConnection();
            if (conn == null) {
                return invoices;
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InvoiceInfo invoice = new InvoiceInfo(
                        rs.getInt("invoice_id"),
                        rs.getInt("booking_id"),
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getBigDecimal("total_price"),
                        rs.getString("event_names"),
                        rs.getString("client_name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("postcode"),
                        rs.getString("email")
                );
                invoices.add(invoice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoices;
    }

    /**
     * Fetches all contracts to be displayed on the contracts screen
     * @return list of contracts
     */
    public static List<ContractInfo> getContracts() {
        List<ContractInfo> contracts = new ArrayList<>();

        String sql = "SELECT b.booking_id, b.booking_name, b.start_date, b.end_date, ct.signed_date, ct.total_price, b.status, cl.client_id, cl.company_name, " +
                "CONCAT(cl.contact_first_name, ' ', cl.contact_last_name) AS client_name, cl.phone_number, cl.email, b.contract_id " +
                "FROM Bookings b " +
                "JOIN Contracts ct ON b.contract_id = ct.contract_id " +
                "JOIN Clients cl ON ct.client_id = cl.client_id " +
                "WHERE b.status='confirmed'";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return contracts;
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ContractInfo contract = new ContractInfo(
                        rs.getInt("booking_id"),
                        rs.getString("booking_name"),
                        rs.getInt("contract_id"),
                        rs.getInt("client_id"),
                        rs.getDate("signed_date").toLocalDate(),
                        rs.getBigDecimal("total_price"),
                        rs.getString("status"),
                        rs.getString("company_name"),
                        rs.getString("client_name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate()
                );
                contracts.add(contract);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contracts;
    }

    /**
     * Updates a booking's status to the given status string
     * @param bookingID unique identifier for a booking
     * @param newStatus status that the booking's status will change to
     * @throws SQLException
     */
    public static void updateBookingStatus(int bookingID, String newStatus) throws SQLException {
        String updateQuery = "UPDATE Bookings SET status = ? WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, bookingID);
            stmt.executeUpdate();
        }
    }

    /**
     * Fetches all the reviews for an event from the database
     * @param bookingId unique identifier for a booking
     * @param eventId identifier to determine which event to get reviews for
     * @return list of reviews to be displayed on the reviews screen
     */
    public static List<ReviewsInfo> getReviewsForEvent(int bookingId, int eventId) {
        List<ReviewsInfo> reviews = new ArrayList<>();
        String sql = "SELECT reviewer_name, rating, comment, date_posted " +
                "FROM Reviews " +
                "WHERE booking_id = ? AND event_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            stmt.setInt(2, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ReviewsInfo review = new ReviewsInfo(
                        bookingId,
                        eventId,
                        rs.getString("reviewer_name"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getDate("date_posted").toLocalDate()
                );
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reviews for event: " + e.getMessage());
        }

        return reviews;
    }

    /**
     * Fetches all events from the database
     * @return list of events to be displayed on the reviews screen so user can pick an event to view reviews for
     */
    public static List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.booking_id, e.event_id, e.name, e.type, e.start, e.end, e.max_discount, e.venue_id, " +
                "v.name as venue_name, e.client_id, c.company_name AS client_name " +
                "FROM Events e " +
                "JOIN Clients c ON e.client_id = c.client_id " +
                "JOIN Venues v ON e.venue_id = v.venue_id " +
                "JOIN Bookings b ON e.booking_id = b.booking_id " +
                "WHERE b.status != 'Cancelled'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("booking_id"),
                        rs.getInt("event_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("client_name"),
                        rs.getTimestamp("start").toLocalDateTime(),
                        rs.getTimestamp("end").toLocalDateTime(),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        rs.getDouble("max_discount"),
                        rs.getInt("venue_id"),
                        rs.getString("venue_name"),
                        null,
                        null
                );
                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching events for Reviews: " + e.getMessage());
        }

        return events;
    }

    /**
     * Helper class to store clientID and companyName
     *  Used while saving a booking and for contract details
     */
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

    /**
     * Gets all diary notes from within a given time frame
     * @param timeframeStart start date of time frame
     * @param timeframeEnd end date of time frame
     * @return HashMap mapping the date to its note
     */
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

    /**
     * Stores a given note in the database
     * @param diaryNote the note to be stored
     */
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

    /**
     * Deletes a given note from the database
     * @param diaryNote the note to be deleted
     */
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



