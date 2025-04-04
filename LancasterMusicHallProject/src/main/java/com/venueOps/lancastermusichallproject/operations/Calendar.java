package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Calendar implements ICalendar {

    @FXML private GridPane calendarGrid;
    @FXML private Label monthYearLabel;

    private YearMonth currentYearMonth;
    private final ArrayList<IEvent> events = new ArrayList<>();

    public Calendar() {
        currentYearMonth = YearMonth.now(); // Start with current month
    }

    @FXML
    private void initialize() {
        updateCalendar();
    }

    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

    public void NewBooking() { ScreenController.loadScreen("BookingOverview"); }

    public void ViewBookings() {};

    @FXML
    private void goToPreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void goToNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }

    private void updateCalendar() {
        calendarGrid.getChildren().clear();

        // Update title
        monthYearLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int col = dayOfWeek - 1;
        int row = 1;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = currentYearMonth.atDay(day);

            Button dayButton = new Button(String.valueOf(day));
            dayButton.setPrefSize(114, 85);

            // Highlight todays dat
            if (currentDate.equals(LocalDate.now())) {
                dayButton.setStyle("-fx-border-color: #3366FF; -fx-border-width: 2px;");
            }

            // Color if event exists
            if (!isAvailable(currentDate)) {
                dayButton.setStyle("-fx-background-color: #FFDAB9;"); // Peach for event day
            }

            // Click day to view or add event
            dayButton.setOnAction(e -> openDayPopup(currentDate));

            calendarGrid.add(dayButton, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    // Method to show popup when clicking a day
    private void openDayPopup(LocalDate date) {
        // optional: save clicked date to a shared variable or service for later use
        // for now, just switch screen

        AppData.setSelectedDate(date);
        Diary diaryController = (Diary) ScreenController.getController("Diary");
        if (diaryController != null) {
            diaryController.refresh();
        }
        ScreenController.loadScreen("Diary");
    }

    // Refresh Calendar
    public void refreshCalendar() {
        updateCalendar();
    }

    // Generates unique event ID
    private int generateEventID() {
        return events.size() == 0 ? 1 : events.get(events.size() - 1).getEventID() + 1;
    }

    // Interface Methods from ICalendar
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
}
