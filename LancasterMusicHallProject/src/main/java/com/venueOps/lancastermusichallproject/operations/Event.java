package com.venueOps.lancastermusichallproject.operations;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

public class Event implements IEvent {
    private int eventID;
    private String eventName;
    private String eventType;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private float eventPrice;
    private int venueID;
    private String venueName;
    private int seatingConfigID;

    public Event(int eventID, String eventName, String eventType, LocalDateTime eventStart, LocalDateTime eventEnd, float eventPrice, int venueID, String venueName) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventPrice = eventPrice;
        this.venueID = venueID;
        this.venueName = venueName;

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
    public LocalDateTime getEventStart() { return this.eventStart; }
    @Override
    public void setEventStart(LocalDateTime eventStart) { this.eventStart = eventStart; }

    @Override
    public LocalDateTime getEventEnd() { return this.eventEnd; }
    @Override
    public void setEventEnd(LocalDateTime eventEnd) { this.eventEnd = eventEnd; }

    @Override
    public float getEventPrice() { return this.eventPrice; }
    @Override
    public void setEventPrice(float eventPrice) { this.eventPrice = eventPrice; }

    @Override
    public int getVenueID() { return this.venueID; }
    @Override
    public void setVenueID(int venueID) { this.venueID = venueID; }

    @Override
    public String getVenueName() { return this.venueName; }
    @Override
    public void setVenueName(String venueName) { this.venueName = venueName; }

    // A setter for SeatingConfigID isn't provided as it is automatically assigned within the Event's constructor
    @Override
    public int getSeatingConfigID() { return seatingConfigID; }

}

