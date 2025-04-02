package com.venueOps.lancastermusichallproject.operations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Event implements IEvent {
    private int eventID;
    private String eventName;
    private String eventType;
    private String eventHost;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private BigDecimal eventPrice;
    private int venueID;
    private String venueName;
    private int seatingConfigID;
    private Map<LocalDate, Integer> dailyTicketSales;

    public Event(int eventID, String eventName, String eventType, String eventHost, LocalDateTime eventStart,
                 LocalDateTime eventEnd, BigDecimal eventPrice, int venueID, String venueName, Map<LocalDate, Integer> dailyTicketSales) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventHost = eventHost;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventPrice = eventPrice;
        this.venueID = venueID;
        this.venueName = venueName;
        this.dailyTicketSales = dailyTicketSales != null ? dailyTicketSales : new HashMap<>();

        // Store seatingConfigID automatically
        this.seatingConfigID = SeatingConfig.getSeatingConfigID(venueID, eventType);
    }

    // Getters and setters for each attribute
    @Override
    public int getEventID() { return this.eventID; }
    @Override
    public void setEventID(int eventID) { this.eventID = eventID; }

    @Override
    public String getEventName() { return this.eventName; }
    @Override
    public void setEventName(String eventName) { this.eventName = eventName; }

    @Override
    public String getEventType() { return this.eventType; }
    @Override
    public void setEventType(String eventType) { this.eventType = eventType; }

    @Override
    public String getEventHost() { return this.eventHost; }
    @Override
    public void setEventHost(String eventHost) { this.eventHost = eventHost; }

    @Override
    public LocalDateTime getEventStart() { return this.eventStart; }
    @Override
    public void setEventStart(LocalDateTime eventStart) { this.eventStart = eventStart; }

    @Override
    public LocalDateTime getEventEnd() { return this.eventEnd; }
    @Override
    public void setEventEnd(LocalDateTime eventEnd) { this.eventEnd = eventEnd; }

    @Override
    public BigDecimal getEventPrice() { return this.eventPrice; }
    @Override
    public void setEventPrice(BigDecimal eventPrice) { this.eventPrice = eventPrice; }

    @Override
    public int getVenueID() { return this.venueID; }
    @Override
    public void setVenueID(int venueID) { this.venueID = venueID; }

    @Override
    public String getVenueName() { return this.venueName; }
    @Override
    public void setVenueName(String venueName) { this.venueName = venueName; }

    @Override
    public Map<LocalDate, Integer> getDailyTicketSales() { return dailyTicketSales; }
    @Override
    public void setDailyTicketSales(Map<LocalDate, Integer> dailyTicketSales) { this.dailyTicketSales = dailyTicketSales; }

    // A setter for SeatingConfigID isn't provided as it is automatically assigned within the Event's constructor
    @Override
    public int getSeatingConfigID() { return seatingConfigID; }

    // Helper for getting ticket sales for specific day
    public int getTicketsSoldForDay(LocalDate date) {
        return dailyTicketSales.getOrDefault(date, 0);
    }

    // Helper for getting total tickets sold for event
    public int getTotalTicketsSold() {
        return dailyTicketSales.values().stream().mapToInt(Integer::intValue).sum();
    }
}

