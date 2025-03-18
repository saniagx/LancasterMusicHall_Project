package com.venueOps.lancastermusichallproject.operations;


import java.sql.Date;
import java.sql.Time;

public interface IEvent {

    // Getters and setters for each attribute
    int getEventID();
    void setEventID(int eventID);

    String getEventName();
    void setEventName(String eventName);

    String getEventType();
    void setEventType(String eventType);

    Date getEventDate();
    void setEventDate(Date eventDate);

    Time getEventStartTime();
    void setEventStartTime(Time eventStartTime);

    Time getEventEndTime();
    void setEventEndTime(Time eventEndTime);

    float getEventPrice();
    void setEventPrice(float eventPrice);

    int getVenueID();
    void setVenueID(int venueID);

    String getVenueName();
    void setVenueName(String venueName);

    // A setter for SeatingConfigID isn't provided as it is automatically assigned within the Event's constructor
    int getSeatingConfigID();
}
