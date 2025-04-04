package com.venueOps.lancastermusichallproject.database;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.venueOps.lancastermusichallproject.operations.Client;
import com.venueOps.lancastermusichallproject.operations.Event;
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
            String eventsQuery = "SELECT e.booking_id, e.event_id, e.name, e.type, e.start, e.end, e.price, e.max_discount, e.venue_id, v.name as venue_name, e.client_id, c.company_name AS client_name " +
                    "FROM Events e " +
                    "JOIN Clients c ON e.client_id = c.client_id " +
                    "JOIN Venues v ON e.venue_id = v.venue_id " +
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
                BigDecimal price = BigDecimal.valueOf(eventRs.getDouble("price"));
                double max_discount = Double.parseDouble(eventRs.getString("max_discount"));
                int venueID = eventRs.getInt("venue_id");
                String venueName = eventRs.getString("venue_name");

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

                Event event = new Event(bookingID, eventID, name, type, client, startTimestamp, endTimestamp, price, max_discount, venueID, venueName, dailyTicketSales);
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
            String eventQuery = "SELECT e.booking_id, e.event_id, e.name, e.type, e.start, e.end, e.price, e.max_discount, e.venue_id, v.name as venue_name, e.client_id, c.company_name AS client_name " +
                    "FROM Events e " +
                    "JOIN Clients c ON e.client_id = c.client_id " +
                    "JOIN Venues v ON e.venue_id = v.venue_id " +
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
                BigDecimal price = BigDecimal.valueOf(eventRs.getDouble("price"));
                double max_discount = Double.parseDouble(eventRs.getString("max_discount"));
                int venueID = eventRs.getInt("venue_id");
                String venueName = eventRs.getString("venue_name");

                Event event = new Event(bookingID, eventID, name, type, client, start, end, price, max_discount, venueID, venueName, null);
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
}
