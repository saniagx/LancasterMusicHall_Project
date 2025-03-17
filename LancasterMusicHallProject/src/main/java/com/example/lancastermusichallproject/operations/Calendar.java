package com.example.lancastermusichallproject.operations;

import java.sql.Date;
import java.util.ArrayList;

public class Calendar implements ICalendar {
    private ArrayList<IEvent> events = new ArrayList<>();

    public Calendar() {}

    /**
     * Adds event to the events ArrayList
     * @param event Event object to be added
     */
    @Override
    public void addEvent(IEvent event) {
        events.add(event);
    }

    /**
     * Removes event from the events ArrayList according to the given ID
     * @param eventID Unique identifier for event objects
     */
    @Override
    public void removeEvent(int eventID) {
        events.removeIf(event -> event.getEventID() == eventID);
    }

    /**
     * Returns event object matching the given ID
     * @param eventID Unique identifier for event objects
     * @return Event object or null if no Event in the ArrayList has the given ID
     */
    @Override
    public IEvent getEvent(int eventID) {
        for (IEvent event : events) {
            if (event.getEventID() == eventID)
                return event;
        }
        return null;
    }

    /**
     * Returns the events ArrayList
     * @return ArrayList containing all the events in the calendar
     */
    @Override
    public ArrayList<IEvent> getAllEvents() {
        return events;
    }

    /**
     * Returns true if the date given doesn't have an event booked on that date
     * @param date SQL data type
     * @return Boolean which is true if a date is available to be booked
     */
    @Override
    public boolean isAvailable(Date date) {
        for (IEvent event : events) {
            if (event.getEventDate().equals(date))
                return false; // Date is already booked
        }
        return true; // Date is available
    }
}