package com.venueOps.lancastermusichallproject.marketing;

/**
 * Represents reserved seating information for an event.
 */
public class Seating {
    private String[] reservedSeatsList;
    private int holdTime;
    private String eventType;

    // Constructor
    public Seating(String[] reservedSeatsList, int holdTime, String eventType) {
        this.reservedSeatsList = reservedSeatsList;
        this.holdTime = holdTime;
        this.eventType = eventType;
    }

    // Returns reserved seats list
    public String[] getReservedSeatsList() {
        return reservedSeatsList;
    }

    // Returns the number of days the seats are held
    public int getHoldTime() {
        return holdTime;
    }

    // Returns the event type (e.g., Film, Live Show)
    public String getEventType() {
        return eventType;
    }
}
