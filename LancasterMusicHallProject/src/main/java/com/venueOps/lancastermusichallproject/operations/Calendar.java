package com.venueOps.lancastermusichallproject.operations;

import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.DetailedWeekView;
import com.calendarfx.view.MonthView;
import com.venueOps.lancastermusichallproject.ScreenController;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Calendar implements ICalendar {
    @FXML private Pane calendarContainer;
    @FXML private Text dateLabel;

    private DetailedWeekView weekView;
    private MonthView monthView;
    private com.calendarfx.model.Calendar eventsCalendar;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    private ArrayList<IEvent> events = new ArrayList<>();

    public Calendar() {}

    @FXML
    private void initialize() {
        eventsCalendar = new com.calendarfx.model.Calendar("Events");
        CalendarSource calendarSource = new CalendarSource("Calendar");
        calendarSource.getCalendars().add(eventsCalendar);

        weekView = new DetailedWeekView();
        weekView.getCalendarSources().setAll(calendarSource);
        weekView.setPrefSize(480, 480);

        monthView = new MonthView();
        monthView.getCalendarSources().setAll(calendarSource);
        monthView.setPrefSize(480, 480);

        updateDateLabel();

        // Default to Monthly view
        calendarContainer.getChildren().setAll(monthView);
    }

    @FXML
    private void WeeklyView() {
        calendarContainer.getChildren().setAll(weekView);
        updateDateLabel();
    }

    @FXML
    private void MonthlyView() {
        calendarContainer.getChildren().setAll(monthView);
        updateDateLabel();
    }

    @FXML
    private void Previous() {
        if (calendarContainer.getChildren().contains(weekView)) {
            LocalDate currentStart = weekView.getStartDate();
            weekView.setDate(currentStart.minusWeeks(1));
        } else if (calendarContainer.getChildren().contains(monthView)) {
            LocalDate currentDate = monthView.getDate();
            monthView.setDate(currentDate.minusMonths(1));
        }
        updateDateLabel();
    }

    @FXML
    private void Next() {
        if (calendarContainer.getChildren().contains(weekView)) {
            LocalDate currentStart = weekView.getStartDate();
            weekView.setDate(currentStart.plusWeeks(1));
        } else if (calendarContainer.getChildren().contains(monthView)) {
            LocalDate currentDate = monthView.getDate();
            monthView.setDate(currentDate.plusMonths(1));
        }
        updateDateLabel();
    }

    private void updateDateLabel() {
        LocalDate date;
        if (calendarContainer.getChildren().contains(weekView)) {
            date = weekView.getStartDate();
        } else {
            date = monthView.getDate();
        }
        dateLabel.setText(date.format(dateFormatter));
    }

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
     * @param date LocalDate data type
     * @return Boolean which is true if a date is available to be booked
     */
    @Override
    public boolean isAvailable(LocalDate date) {
        for (IEvent event : events) {
            LocalDate start = event.getEventStart().toLocalDate();
            LocalDate end = event.getEventEnd().toLocalDate();

            if (!date.isBefore(start) && !date.isAfter(end)) {
                return false; // Date falls within the event range
            }
        }
        return true; // Date is available
    }

    public void BackButton() { ScreenController.loadScreen("MainMenu"); }
}