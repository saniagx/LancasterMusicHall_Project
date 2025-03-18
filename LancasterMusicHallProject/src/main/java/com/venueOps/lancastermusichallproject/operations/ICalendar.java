package com.venueOps.lancastermusichallproject.operations;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ICalendar {

    /**
     * Adds event to the events ArrayList
     * @param event Event object to be added
     */
    void addEvent(IEvent event);

    /**
     * Removes event from the events ArrayList according to the given ID
     * @param eventID Unique identifier for event objects
     */
    void removeEvent(int eventID);

    /**
     * Returns event object matching the given ID
     * @param eventID Unique identifier for event objects
     * @return Event object or null if no Event in the ArrayList has the given ID
     */
    IEvent getEvent(int eventID);

    /**
     * Returns the events ArrayList
     * @return ArrayList containing all the events in the calendar
     */
    ArrayList<IEvent> getAllEvents();

    /**
     * Returns true if the date given doesn't have an event booked on that date
     * @param date LocalDate data type
     * @return Boolean which is true if a date is available to be booked
     */
    boolean isAvailable(LocalDate date);
}
