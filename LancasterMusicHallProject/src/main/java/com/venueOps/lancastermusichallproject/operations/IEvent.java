package com.venueOps.lancastermusichallproject.operations;

import java.time.LocalDateTime;

public interface IEvent {

    // Getters and setters for each attribute
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

    float getEventPrice();
    void setEventPrice(float eventPrice);

    int getVenueID();
    void setVenueID(int venueID);

    String getVenueName();
    void setVenueName(String venueName);

    // A setter for SeatingConfigID isn't provided as it is automatically assigned within the Event's constructor
    int getSeatingConfigID();
}
