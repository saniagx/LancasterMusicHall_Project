package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Calendar implements ICalendar {

    @FXML private GridPane calendarGrid;
    @FXML private Label monthYearLabel;
    @FXML private TextArea diaryPreviewArea;

    private YearMonth currentYearMonth;
    private List<Booking> bookings = new ArrayList<>();
    private List<DiaryNote> diaryNotes = new ArrayList<>();

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

    public void NewBooking() { ScreenController.loadScreen("NewBooking"); }

    public void ViewBookings() {
        BookingsOverview bookingsOverviewController = (BookingsOverview) ScreenController.getController("BookingsOverview");
        if (bookingsOverviewController != null) {
            bookingsOverviewController.refresh();
        }
        ScreenController.loadScreen("BookingsOverview");
    }

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
        bookings = DatabaseConnection.getBookings();
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

            // Highlight today's date
            if (currentDate.equals(LocalDate.now())) {
                dayButton.setStyle("-fx-border-color: #3366FF; -fx-border-width: 2px;");
            }

            // Color if booking exists
            int bookedVenues = 0;
            for (String venue : AppData.getVenues()) {
                if (!isVenueAvailable(currentDate.atStartOfDay(), currentDate.atTime(23, 59, 59), venue)) {
                    bookedVenues++;
                }
            }

            if (bookedVenues > 0 && bookedVenues < AppData.getVenues().size()) {
                // Green for good availability
                dayButton.setStyle("-fx-background-color: #B9FFC2;");
            } else if (bookedVenues >= AppData.getVenues().size() - 3 && bookedVenues < AppData.getVenues().size()) {
                // Yellow for limited availability
                dayButton.setStyle("-fx-background-color: #F0D680;");
            } else if (bookedVenues == AppData.getVenues().size()) {
                // Red for no availability
                dayButton.setStyle("-fx-background-color: #F08080;");
            }

            // Click day to add diary note
            dayButton.setOnAction(e -> openDiary(currentDate));

            calendarGrid.add(dayButton, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
        updateDiaryPreviewPanel();
    }

    // Method to show diary when clicking a day
    private void openDiary(LocalDate date) {
        AppData.setSelectedDate(date);
        Diary diaryController = (Diary) ScreenController.getController("Diary");
        if (diaryController != null) {
            diaryController.refresh();
        }
        ScreenController.loadScreen("Diary");
    }

    // Diary Preview Panel
    private void updateDiaryPreviewPanel() {
        if (diaryPreviewArea != null) {
            StringBuilder preview = new StringBuilder();
            for (Map.Entry<String, String> entry : AppData.getAllNotes().entrySet()) {
                preview.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n\n");
            }

            diaryPreviewArea.setText(preview.isEmpty() ? "No notes found" : preview.toString());
        }
    }

    // Refresh Calendar
    public void refreshCalendar() {
        updateCalendar();
    }

    // Interface Methods from ICalendar
    @Override
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    @Override
    public void removeBooking(int bookingID) {
        bookings.removeIf(booking -> booking.getBookingID() == bookingID);
    }

    @Override
    public List<IEvent> getEvents(Booking booking) {
        return booking.getEvents();
    }

    @Override
    public List<Booking> getBookings() {
        return bookings;
    }

    @Override
    public boolean isVenueAvailable(LocalDateTime start, LocalDateTime end, String venueName) {
        for (Booking booking : bookings) {
            for (IEvent event : booking.getEvents()) {
                if (event.getVenueName().equals(venueName)) {
                    LocalDateTime eventStart = event.getEventStart();
                    LocalDateTime eventEnd = event.getEventEnd();

                    if (start.isBefore(eventEnd) && end.isAfter(eventStart)) {
                        return false; // Overlap so venue is not available
                    }
                }
            }
        }
        return true;
    }
}
