package com.venueOps.lancastermusichallproject.operationsDB_interface;

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

    public static void main(String[] args) {
        try {
            JDBC jdbc = new JDBC();
            System.out.println("JDBC Initialised");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

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

    /**
     * Returns a list of Booking objects. Bookings contain multiple events accessible via .getEvents()
     * Events contain a SeatingConfig accessible via .getSeatingConfig
     * The restricted views within the SeatingConfig via
     *
     * @param timeframeStart LocalDate determining the start of the time frame from which to fetch bookings
     * @param timeframeEnd LocalDate determining the end of the time frame from which to fetch bookings
     * @return  List containing bookings
     * @throws Exception
     */
    public List<Booking> getBookings(LocalDate timeframeStart, LocalDate timeframeEnd) throws Exception {
        return databaseMethods.getBookings(connection, timeframeStart, timeframeEnd);
    }

    /**
     * Adds a film booking hosted by Lancaster's Music Hall to the calendar and database
     * @param eventName Name of the event
     * @param startDateTime LocalDateTime determining when the event is
     * @param endDateTime LocalDateTime determining when the event ends
     * @param ticketPrice Price of the tickets as a BigDecimal
     * @param maxDiscount Maximum discount that can be applied to ticket prices
     * @param venueName Name of the venue, must be "Main Hall" or "Small Hall"
     * @return
     * @throws Exception
     */
    public boolean addFilmBooking(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                  BigDecimal ticketPrice, double maxDiscount, String venueName) throws Exception {
        return databaseMethods.addFilmBooking(connection, eventName, startDateTime, endDateTime, ticketPrice, maxDiscount, venueName);
    }

    /**
     * Returns true if a venue can be used between the given time frame
     *
     * @param start LocalDateTime determining the start of the timeframe to search between
     * @param end LocalDateTime determining the end of the timeframe to search between
     * @param venueName Name of the venue
     * @return
     * @throws Exception
     */
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
