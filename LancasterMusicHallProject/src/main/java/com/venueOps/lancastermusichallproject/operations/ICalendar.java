package com.venueOps.lancastermusichallproject.operations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface ICalendar {

    void addBooking(Booking booking);
    void removeBooking(int bookingID);
    List<IEvent> getEvents(Booking booking);
    List<Booking> getBookings();
    boolean isVenueAvailable(LocalDateTime startDate, LocalDateTime end, String venueName);
}
