package com.venueOps.lancastermusichallproject.operations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Event implements IEvent {
    private int bookingID;
    private int eventID;
    private String eventName;
    private String eventType;
    private String eventHost;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private BigDecimal eventPrice;
    private BigDecimal ticketPrice;
    private double maxDiscount;
    private int venueID;
    private String venueName;
    private Map<LocalDate, Integer> dailyTicketSales;
    //private int seatingConfigID;
    private SeatingConfig seatingConfig;

    public Event(int bookingID, int eventID, String eventName, String eventType, String eventHost, LocalDateTime eventStart,
                 LocalDateTime eventEnd, BigDecimal eventPrice, BigDecimal ticketPrice, double maxDiscount, int venueID, String venueName,
                 Map<LocalDate, Integer> dailyTicketSales, SeatingConfig seatingConfig) {
        this.bookingID = bookingID;
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventHost = eventHost;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventPrice = eventPrice;
        this.ticketPrice = ticketPrice;
        this.maxDiscount = maxDiscount;
        this.venueID = venueID;
        this.venueName = venueName;
        this.dailyTicketSales = dailyTicketSales != null ? dailyTicketSales : new HashMap<>();
        this.seatingConfig = seatingConfig;

        // Store seatingConfigID automatically
        //this.seatingConfigID = -1;
    }

    // Getters and setters for each attribute
    @Override
    public int getBookingID() { return this.bookingID; }
    @Override
    public void setBookingID(int bookingID) { this.bookingID = bookingID; }

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
    public BigDecimal getTicketPrice() { return this.ticketPrice; }
    @Override
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }

    @Override
    public double getMaxDiscount() { return this.maxDiscount; }
    @Override
    public void setMaxDiscount(double maxDiscount) { this.maxDiscount = maxDiscount; }

    @Override
    public int getVenueID() { return venueID; }
    @Override
    public void setVenueID(int venueID) { this.venueID = venueID; }

    @Override
    public String getVenueName() { return venueName; }
    @Override
    public void setVenueName(String venueName) { this.venueName = venueName; }

    @Override
    public Map<LocalDate, Integer> getDailyTicketSales() { return dailyTicketSales; }
    @Override
    public void setDailyTicketSales(Map<LocalDate, Integer> dailyTicketSales) { this.dailyTicketSales = dailyTicketSales; }

    @Override
    public SeatingConfig getSeatingConfig() {
        return seatingConfig;
    }

    @Override
    public void setSeatingConfig(SeatingConfig seatingConfig) {
        this.seatingConfig = seatingConfig;
    }

    // A setter for SeatingConfigID isn't provided as it is automatically assigned within the Event's constructor
    //@Override
    //public int getSeatingConfigID() { return seatingConfigID; }

    // Get ticket sales for specific day
    public int getTicketsSoldForDay(LocalDate date) {
        return dailyTicketSales.getOrDefault(date, -1);
    }

    // Helper for getting total tickets sold for event
    public int getTotalTicketsSold() {
        int total = 0;
        for (Integer tickets : dailyTicketSales.values()) {
            if (tickets != -1) {
                total += tickets;
            }
        }
        return total;
    }

    // Calculate the number of days tickets were being sold
    public int getDaysWithTicketSales() {
        int days = 0;
        for (Integer tickets : dailyTicketSales.values()) {
            if (tickets != -1) {
                days++;
            }
        }
        return days;
    }
}

