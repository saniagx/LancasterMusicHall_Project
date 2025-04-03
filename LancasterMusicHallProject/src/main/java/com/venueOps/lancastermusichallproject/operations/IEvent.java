package com.venueOps.lancastermusichallproject.operations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public interface IEvent {

    // Getters and setters for each attribute
    int getBookingID();
    void setBookingID(int bookingID);

    int getEventID();
    void setEventID(int eventID);

    String getEventName();
    void setEventName(String eventName);

    String getEventType();
    void setEventType(String eventType);

    String getEventHost();
    void setEventHost(String eventHost);

    LocalDateTime getEventStart();
    void setEventStart(LocalDateTime eventStart);

    LocalDateTime getEventEnd();
    void setEventEnd(LocalDateTime eventEnd);

    BigDecimal getEventPrice();
    void setEventPrice(BigDecimal eventPrice);

    double getMaxDiscount();
    void setMaxDiscount(double maxDiscount);

    int getVenueID();
    void setVenueID(int venueID);

    String getVenueName();
    void setVenueName(String venueName);

    Map<LocalDate, Integer> getDailyTicketSales();
    void setDailyTicketSales(Map<LocalDate, Integer> dailyTicketsSold);

    // A setter for SeatingConfigID isn't provided as it is automatically assigned within the Event's constructor
    int getSeatingConfigID();
}
