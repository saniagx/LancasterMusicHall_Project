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
import java.util.*;

public class Calendar implements ICalendar {

    @FXML private GridPane calendarGrid;
    @FXML private Label monthYearLabel;
    @FXML private TextArea diaryPreviewArea;

    private YearMonth currentYearMonth;
    private List<Booking> bookings = new ArrayList<>();
    private HashMap<String, String> diaryMap = new HashMap<>();

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
            bookingsOverviewController.populateBookingsTable();
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
            dayButton.setPrefSize(100, 80);
            dayButton.setOnMouseEntered(e -> dayButton.setStyle(dayButton.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0.1, 0, 2);"));
            dayButton.setOnMouseExited(e -> dayButton.setStyle(dayButton.getStyle().replaceAll("-fx-effect:.*?;", "")));

            // Highlight today's date
            if (currentDate.equals(LocalDate.now())) {
                dayButton.setStyle("""
                        -fx-background-color: #ffffff;
                        -fx-background-radius: 12;
                        -fx-border-radius: 12;
                        -fx-border-color: #CCCCCC;
                        -fx-font-size: 16px;
                        -fx-font-weight: bold;
                        -fx-text-fill: #333333;
                    """);
            }

            // Color if booking exists
            int bookedVenues = 0;
            for (String venue : AppData.getVenues()) {
                if (!isVenueAvailable(currentDate.atStartOfDay(), currentDate.atTime(23, 59, 59), venue)) {
                    bookedVenues++;
                }
            }

            String baseStyle = """
                -fx-background-color: #ffffff;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-border-color: #CCCCCC;
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-text-fill: #333333;
                """;

            if (bookedVenues > 0 && bookedVenues < AppData.getVenues().size()) {
                baseStyle += "-fx-background-color: #b9ffc2;";  // light green
            } else if (bookedVenues >= AppData.getVenues().size() - 3) {
                baseStyle += "-fx-background-color: #f0d680;";  // light yellow
            } else if (bookedVenues == AppData.getVenues().size()) {
                baseStyle += "-fx-background-color: #f08080;";  // light red
            }

            dayButton.setStyle(baseStyle);


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
            preview.append("Notes for upcoming dates: \n\n");

            AppData.getAllNotes().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()) // Sort notes by soonest first
                    .forEach(entry -> {
                        LocalDate date = LocalDate.parse(entry.getKey());
                        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); // Format dates
                        preview.append(formattedDate).append(": ").append(entry.getValue()).append("\n\n");
                    });

            diaryPreviewArea.setText(preview.isEmpty() ? "No notes found" : preview.toString());
        }
    }

    // Refresh Calendar
    public void refreshCalendar() {
        diaryMap = DatabaseConnection.getDiaryNotes(LocalDate.now(), YearMonth.now().atEndOfMonth().plusMonths(1));
        AppData.loadNotes(diaryMap);
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
            if (booking.getStatus().equals("Cancelled")) {
                continue; // Skip cancelled bookings
            }
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
