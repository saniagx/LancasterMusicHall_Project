package com.venueOps.lancastermusichallproject.operationsDB_interface;

import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import com.venueOps.lancastermusichallproject.operations.Booking;
import com.venueOps.lancastermusichallproject.operations.IEvent;
import com.venueOps.lancastermusichallproject.operations.Event;
import com.venueOps.lancastermusichallproject.operations.Client;
import com.venueOps.lancastermusichallproject.operations.SeatingConfig;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseMethods implements DatabaseInterface {

    public List<Booking> getBookings(Connection conn, LocalDate timeframeStart, LocalDate timeframeEnd) throws Exception {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.booking_id, b.booking_name, cl.company_name, cl.contact_first_name, cl.contact_last_name, " +
                "ct.signed_date, b.start_date, b.end_date, b.status " +
                "FROM Bookings b " +
                "JOIN Contracts ct ON b.contract_id = ct.contract_id " +
                "JOIN Clients cl ON ct.client_id = cl.client_id " +
                "WHERE (b.start_date <= ? AND b.end_date >= ?)";
        try {
            if (conn == null) {
                return bookings;
            }
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, Date.valueOf(timeframeEnd));
            stmt.setDate(2, Date.valueOf(timeframeStart));
            ResultSet rs = stmt.executeQuery();
            {
                while (rs.next()) {
                    int bookingID = rs.getInt("booking_id");
                    String bookingName = rs.getString("booking_name");
                    String companyName = rs.getString("company_name");
                    String contact_FName = rs.getString("contact_first_name");
                    String contact_LName = rs.getString("contact_last_name");
                    LocalDate signedDate = rs.getDate("signed_date").toLocalDate();
                    LocalDate startDate = rs.getDate("start_date").toLocalDate();
                    LocalDate endDate = rs.getDate("end_date").toLocalDate();
                    String status = rs.getString("status");

                    List<IEvent> events = getEventsForBooking(conn, bookingID, companyName);
                    Client client = new Client(0, companyName, contact_FName, contact_LName, null, null, null, null, null);

                    Booking booking = new Booking(bookingID, bookingName, events, client, signedDate, BigDecimal.ZERO, startDate, endDate, status);
                    bookings.add(booking);
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Failed to get bookings: " + e.getMessage());
            throw e;
        }
        return bookings;
    }

    public boolean addFilmBooking(Connection conn, String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime, BigDecimal ticketPrice, double maxDiscount, String venueName) throws Exception{
        try {
            if (conn == null) {
                return false;
            }
            int capacity;
            String layout;
            int venueID;
            if (venueName.equals("Small Hall")) {
                capacity = 95;
                layout = "Default";
                venueID = 1;
            } else if (venueName.equals("Main Hall")) {
                capacity = 285;
                layout = "No Balconies";
                venueID = 0;
            } else {
                throw new Exception("Invalid venue: " + venueName);
            }

            SeatingConfig seatingConfig = new SeatingConfig(0, capacity, layout, venueName, new ArrayList<>());

            IEvent event = new Event(0, 0, eventName, "Event", "temp", startDateTime,
                    endDateTime, BigDecimal.ZERO, ticketPrice, maxDiscount, venueID, venueName, new HashMap<>(), seatingConfig);
            List<IEvent> events = List.of(event);

            Client client = new Client(0, "Lancaster's Music Hall", "Joe", "Lancaster",
                    "JoeLancaster@email.com", "020 7946 5374", "5374 Main Street", "City, County", "WC2N 5DN");

            Booking booking = new Booking(0, eventName, events, client, LocalDate.now(), BigDecimal.ZERO,
                    event.getEventStart().toLocalDate(), event.getEventEnd().toLocalDate(), "Confirmed");

            try {
                DatabaseConnection.saveBooking(booking);
            } catch (Exception e) {
                System.err.println("Failed to save film booking: " + e.getMessage());
                throw e;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to add film: " + e.getMessage());
            return false;
        }
    }


    public boolean isVenueAvailable(Connection conn, LocalDateTime start, LocalDateTime end, String venueName) throws Exception {
        try {
            List<Booking> bookings = getBookings(conn, start.toLocalDate(), end.toLocalDate());
            for (Booking booking : bookings) {
                for (IEvent event : booking.getEvents()) {
                    if (event.getVenueName().equals(venueName)) {
                        LocalDateTime eventStart = event.getEventStart();
                        LocalDateTime eventEnd = event.getEventEnd();

                        if (start.isBefore(eventEnd) && end.isAfter(eventStart)) {
                            return false; // Overlap so venue is not available
                        }
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to check venue availability: " + e.getMessage());
            throw e;
        }
    }

    // HELPERS
    private List<IEvent> getEventsForBooking(Connection conn, int bookingID, String companyName) throws SQLException {
        List<IEvent> events = new ArrayList<>();
        String eventsQuery = "SELECT e.event_id, e.name, e.type, e.start, e.end, e.ticket_price, e.max_discount, e.venue_id, " +
                "v.name AS venue_name, sc.seating_config_id, sc.capacity, sc.layout " +
                "FROM Events e " +
                "JOIN Venues v ON e.venue_id = v.venue_id " +
                "LEFT JOIN SeatingConfigs sc ON e.seating_config_id = sc.seating_config_id " +
                "WHERE e.booking_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(eventsQuery)) {
            stmt.setInt(1, bookingID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int eventID = rs.getInt("event_id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();
                BigDecimal ticketPrice = rs.getBigDecimal("ticket_price");
                double maxDiscount = rs.getDouble("max_discount");
                int venueID = rs.getInt("venue_id");
                String venueName = rs.getString("venue_name");
                int seatingConfigID = rs.getInt("seating_config_id");
                int capacity = rs.getInt("capacity");
                String layout = rs.getString("layout");

                List<String> restrictedViews = getRestrictedViewsForSeatingConfig(conn, seatingConfigID);

                SeatingConfig seatingConfig = new SeatingConfig(seatingConfigID, capacity, layout, venueName, restrictedViews);

                Event event = new Event(bookingID, eventID, name, type, companyName, start, end,
                        BigDecimal.ZERO, ticketPrice, maxDiscount, venueID, venueName, null, seatingConfig);
                events.add(event);
            }
        }
        return events;
    }

    private List<String> getRestrictedViewsForSeatingConfig(Connection conn, int seatingConfigID) throws SQLException {
        List<String> restrictedViews = new ArrayList<>();

        String query = "SELECT seat_number FROM RestrictedViews WHERE seating_config_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, seatingConfigID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                restrictedViews.add(rs.getString("seat_number"));
            }
        }

        return restrictedViews;
    }
}
