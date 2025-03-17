package com.example.lancastermusichallproject.marketing;

/**
 * Represents a booking enquiry for large events.
 */
public class Booking {
    private int enquiryID;
    private String eventName;
    private String eventType;
    private String eventDate;
    private String startTime;
    private String endTime;
    private int venueID;
    private String venueName;
    private String bookingStatus;

    // Constructor
    public Booking(int enquiryID, String eventName, String eventType,
                   String eventDate, String startTime, String endTime,
                   int venueID, String venueName, String bookingStatus) {
        this.enquiryID = enquiryID;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venueID = venueID;
        this.venueName = venueName;
        this.bookingStatus = bookingStatus;
    }

    // Returns enquiry ID
    public int getEnquiryID() {
        return enquiryID;
    }

    // Returns event name
    public String getEventName() {
        return eventName;
    }

    // Returns event type (e.g., Live Show, Film)
    public String getEventType() {
        return eventType;
    }

    // Returns event date
    public String getEventDate() {
        return eventDate;
    }

    // Returns event start time
    public String getStartTime() {
        return startTime;
    }

    // Returns event end time
    public String getEndTime() {
        return endTime;
    }

    // Returns venue ID
    public int getVenueID() {
        return venueID;
    }

    // Returns venue name
    public String getVenueName() {
        return venueName;
    }

    // Returns the booking status (e.g., Confirmed, Pending)
    public String getBookingStatus() {
        return bookingStatus;
    }
}
