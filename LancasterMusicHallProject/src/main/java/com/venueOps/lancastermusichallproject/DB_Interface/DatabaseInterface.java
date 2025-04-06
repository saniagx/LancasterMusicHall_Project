package com.venueOps.lancastermusichallproject.DB_Interface;

import com.venueOps.lancastermusichallproject.operations.Booking;

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
     */
    List<Booking> getBookings(Connection conn, LocalDate timeframeStart, LocalDate timeframeEnd);

    /**
     * Returns true if a venue can be used between the given time frame
     *
     * @param conn
     * @param start
     * @param end
     * @param venueName
     * @return
     */
    boolean isVenueAvailable(Connection conn, LocalDateTime start, LocalDateTime end, String venueName);
}
