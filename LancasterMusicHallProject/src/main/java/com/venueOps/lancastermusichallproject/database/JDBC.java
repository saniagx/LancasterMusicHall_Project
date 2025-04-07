package com.venueOps.lancastermusichallproject.database;

import com.venueOps.lancastermusichallproject.operations.Booking;
import com.venueOps.lancastermusichallproject.operations.IEvent;
import com.venueOps.lancastermusichallproject.operations.SeatingConfig;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class JDBC {
    private final Connection connection;
    private DatabaseMethods databaseMethods;

    public JDBC() throws SQLException, ClassNotFoundException {
        String host = "sst-stuproj.city.ac.uk";
        String port = "3306";
        String dbName = "in2033t02";
        String user = "in2033t02_d";
        String password = "R4fN0lxD-YM";

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        this.connection = DriverManager.getConnection(url, user, password);
        this.databaseMethods = new DatabaseMethods();
    }

    // Fetches all Bookings within the time frame
    // Bookings contain events, each event has its attributes fetched and set by the database
    // Each event has a Seating Config and has its attributes fetched and set by the database
    public List<Booking> getBookings(LocalDate timeframeStart, LocalDate timeframeEnd) throws Exception {
        return databaseMethods.getBookings(connection, timeframeStart, timeframeEnd);
    }

    public boolean addFilmBooking(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                  BigDecimal ticketPrice, double maxDiscount, String venueName) throws Exception {
        return databaseMethods.addFilmBooking(connection, eventName, startDateTime, endDateTime, ticketPrice, maxDiscount, venueName);
    }

    public boolean isVenueAvailable(LocalDateTime start, LocalDateTime end, String venueName) throws Exception {
        return databaseMethods.isVenueAvailable(connection, start, end, venueName);
    }

    public List<IEvent> getEventsForBooking(Booking booking) {
        return booking.getEvents();
    }

    public SeatingConfig getSeatingConfigForEvent(IEvent event) {
        return event.getSeatingConfig();
    }

    public List<String> getRestrictedViews(SeatingConfig seatingConfig) {
        return seatingConfig.getRestrictedViews();
    }
}
