package com.venueOps.lancastermusichallproject.operationsDB_interface;

import com.venueOps.lancastermusichallproject.operations.Booking;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DatabaseInterface {

    /**
     * Returns a list of Booking objects. Bookings contain multiple events accessible via .getEvents()
     * Events contain a SeatingConfig accessible via .getSeatingConfig
     * The restricted views within the SeatingConfig via
     *
     * @param conn Connection variable for the database
     * @param timeframeStart LocalDate determining the start of the time frame from which to fetch bookings
     * @param timeframeEnd LocalDate determining the end of the time frame from which to fetch bookings
     * @return  List containing bookings
     * @throws Exception
     */
    List<Booking> getBookings(Connection conn, LocalDate timeframeStart, LocalDate timeframeEnd) throws Exception;


    /**
     * Adds a film booking hosted by Lancaster's Music Hall to the calendar and database
     * @param conn Connection variable for the database
     * @param eventName Name of the event
     * @param startDateTime LocalDateTime determining when the event is
     * @param endDateTime LocalDateTime determining when the event ends
     * @param ticketPrice Price of the tickets as a BigDecimal
     * @param maxDiscount Maximum discount that can be applied to ticket prices
     * @param venueName Name of the venue, must be "Main Hall" or "Small Hall"
     * @return
     * @throws Exception
     */
    boolean addFilmBooking(Connection conn, String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime,
                           BigDecimal ticketPrice, double maxDiscount, String venueName) throws Exception;

    /**
     * Returns true if a venue can be used between the given time frame
     *
     * @param conn Connection variable for the database
     * @param start LocalDateTime determining the start of the timeframe to search between
     * @param end LocalDateTime determining the end of the timeframe to search between
     * @param venueName Name of the venue
     * @return
     * @throws Exception
     */
    boolean isVenueAvailable(Connection conn, LocalDateTime start, LocalDateTime end, String venueName) throws Exception;
}
