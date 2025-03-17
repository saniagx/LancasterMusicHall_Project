package com.example.lancastermusichallproject.marketing;

/**
 * Represents a venue where events take place.
 */
public class Venue {
    private int venueID;
    private String venueName;

    // Constructor
    public Venue(int venueID, String venueName) {
        this.venueID = venueID;
        this.venueName = venueName;
    }

    // Returns the venue ID
    public int getVenueID() {
        return venueID;
    }

    // Returns the venue name
    public String getVenueName() {
        return venueName;
    }
}
