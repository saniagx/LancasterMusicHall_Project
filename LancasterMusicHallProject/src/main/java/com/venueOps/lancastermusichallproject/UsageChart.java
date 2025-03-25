package com.venueOps.lancastermusichallproject;


import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import com.venueOps.lancastermusichallproject.operations.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageChart {

    @FXML private TableView<String> venueTable;
    @FXML private TableColumn<String, String> venueColumn;
    @FXML private Canvas chartCanvas;
    @FXML private Canvas timelineCanvas;

    private final List<String> venues = List.of("Main Hall", "Small Hall", "Rehearsal Space", "The Green Room", "Bronte Boardroom", "Dickens Den", "Poe Parlor", "Globe Room", "Chekhov Chamber");
    private GraphicsContext chart_gc;
    private GraphicsContext timeline_gc;
    private int cell_size = 50;

    private LocalDate today;
    private LocalDate currentMonday;
    private LocalDate prevMonday;
    private LocalDate nextMonday;
    private List<LocalDate> weekStarts;
    private ArrayList<Event> events;

    public UsageChart() {}

    @FXML
    public void initialize() {
        // Setup venue list
        venueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        venueColumn.setMaxWidth(120);
        venueColumn.setPrefWidth(TableView.USE_COMPUTED_SIZE);
        venueColumn.setSortable(false);
        venueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        venueTable.setRowFactory(tv -> new TableRow<String>() { // Set row height
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setPrefHeight(cell_size);
            }
        });
        venueTable.getItems().addAll(venues);

        chart_gc = chartCanvas.getGraphicsContext2D();
        timeline_gc = timelineCanvas.getGraphicsContext2D();

        today = LocalDate.now();
        currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        prevMonday = currentMonday.minusWeeks(1);
        nextMonday = currentMonday.plusWeeks(1);
        weekStarts = List.of(prevMonday, currentMonday, nextMonday);

        Refresh();
    }

    // Draws Checkerboard Pattern
    private void drawChartBase() {
        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 9; j++) {
                if ((i + j) % 2 == 0) {
                    chart_gc.setFill(Color.WHITE);
                } else {
                    chart_gc.setFill(Color.LIGHTGRAY);
                }
                chart_gc.fillRect(cell_size*i, cell_size*j, cell_size, cell_size);
            }
        }
    }

    private void drawTimeline() {
        //timeline_gc.clearRect(0, 0, 1049, 64);
        double dayWidth = cell_size;

        timeline_gc.setFill(Color.WHITE);
        timeline_gc.fillRect(0, 0, timelineCanvas.getWidth(), 39);
        for (int i = 0; i < 22; i++) {
            if (i % 2 == 0) {
                timeline_gc.setFill(Color.LIGHTGRAY);
            } else {
                timeline_gc.setFill(Color.WHITE);
            }
            timeline_gc.fillRect(cell_size*i, 39, cell_size, 24);
        }

        // Draw horizontal separators
        timeline_gc.setStroke(Color.BLACK);
        timeline_gc.strokeLine(0, 0, 1049, 0);
        timeline_gc.strokeLine(0, 39, 1049, 39);

        for (LocalDate weekStart : weekStarts) {
            //System.out.println(weekStart);
            double weekX = ChronoUnit.DAYS.between(prevMonday, weekStart) * dayWidth;

            // Draw week label
            timeline_gc.setFill(Color.BLACK);
            timeline_gc.fillText(weekStart.toString(), weekX + 5, 34);

            // Draw vertical separator for weeks
            timeline_gc.strokeLine(weekX, 39, weekX, 0);

            // Draw days for current and previous week
            for (int j = 0; j < 7; j++) {
                LocalDate day = weekStart.plusDays(j);
                double dayX = weekX + (j * dayWidth);

                timeline_gc.fillText(String.valueOf(day.getDayOfMonth()), dayX + 5, 59);

                // Draw vertical separator for days
                timeline_gc.strokeLine(dayX, 64, dayX, 39);
            }
        }
    }

    // To do: Fetch list of events, draw a bar for each of them
    private void drawEvents() {
        drawChartBase();

        for (Event event : events) {
            // Find the venue index (row) in the venues list
            String venueName = event.getVenueName();
            int venueIndex = venues.indexOf(venueName);
            if (venueIndex == -1) {
                continue; // Skip if venue is not in the list
            }

            // Calculate the y-position (center the bar in the cell)
            double y = venueIndex * cell_size + 10;
            double barHeight = cell_size - 20;

            // Calculate start and end positions on the x-axis
            LocalDate eventStartDate = event.getEventStart().toLocalDate();
            LocalDate eventEndDate = event.getEventEnd().toLocalDate();

            // Ensure event falls within the displayed date range
            if (eventStartDate.isBefore(prevMonday)) {
                eventStartDate = prevMonday;
            }
            if (eventEndDate.isAfter(nextMonday.plusDays(6))) {
                eventEndDate = nextMonday.plusDays(6);
            }

            // Calculate the x-position and width
            long startOffset = ChronoUnit.DAYS.between(prevMonday, eventStartDate);
            long duration = ChronoUnit.DAYS.between(eventStartDate, eventEndDate) + 1; // Include the end day
            double x = startOffset * cell_size;
            double width = duration * cell_size;

            // Draw the bar
            chart_gc.setFill(getBarColour(event));
            chart_gc.fillRect(x, y, width, barHeight);
        }
    }

    // Fetches all events for specific time frame
    public ArrayList<Event> getEvents(LocalDate start, LocalDate end) {
        ArrayList<Event> events = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return events;
            }
            String query = "SELECT e.event_id, e.name, e.type, e.start, e.end, e.price, e.venue_id, e.host_id, e.tickets_sold, h.company_name AS host_name, v.name as venue_name " +
                    "FROM Events e " +
                    "JOIN Hosts h ON e.host_id = h.host_id " +
                    "JOIN Venues v ON e.venue_id = v.venue_id " +
                    "WHERE e.start <= ? AND e.end >= ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, end.toString());
            pstmt.setString(2, start.toString());
            ResultSet rs = pstmt.executeQuery();

            // Create event objects and add to array
            while (rs.next()) {
                int eventID = rs.getInt("event_id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String host = rs.getString("host_name");
                LocalDateTime startTimestamp = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime endTimestamp = rs.getTimestamp("end").toLocalDateTime();
                BigDecimal price = BigDecimal.valueOf(rs.getDouble("price"));
                int venueID = rs.getInt("venue_id");
                String venueName = rs.getString("venue_name");
                int ticketsSold = rs.getInt("tickets_sold");

                Event event = new Event(eventID, name, type, host, startTimestamp, endTimestamp, price, venueID, venueName, ticketsSold);
                events.add(event);
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println("Failed to fetch events from database" + e.getMessage());
            return events;
        }
        return events;
    }

    private Color getBarColour(Event event) {
        Map<Integer, Integer> venueCapacity = Map.of(
                0, 374,
                1, 95
        );
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventStart = event.getEventStart();
        LocalDateTime eventEnd = event.getEventEnd();
        double salesPercentage = 0;
        if (event.getVenueID() == 0 || event.getVenueID() == 1) {
            salesPercentage = ((double)event.getTicketsSold() / (double)venueCapacity.get(event.getVenueID())) * 100;
        }

        // Ongoing events are blue
        if (now.isAfter(eventStart) && now.isBefore(eventEnd)) {
            return Color.BLUE;
        }

        // Upcoming events are light blue
        if (eventStart.isAfter(now)) {
            return Color.LIGHTBLUE;
        }

        // Past events below:
        // Meetings are pink
        if (event.getEventType().equals("Meeting")) {
            return Color.PINK;
        }
        // Rehearsal events are purple as they don't have capacity
        if (event.getVenueID() == 2) {
            return Color.MEDIUMPURPLE;
        }
        // Colour of events based on ticket sales
        if (salesPercentage == 100.0) {
            return Color.MEDIUMPURPLE; // Sold out
        } else if (salesPercentage >= 75.0) {
            return Color.LIGHTGREEN; // >= 75% sold
        } else if (salesPercentage >= 50.0) {
            return Color.YELLOW; // >= 50% sold
        } else {
            return Color.RED; // < 50% sold
        }
    }

    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

    public void changeWeek(int weeks) {
        currentMonday = currentMonday.plusWeeks(weeks);
        prevMonday = currentMonday.minusWeeks(1);
        nextMonday = currentMonday.plusWeeks(1);
        weekStarts = List.of(prevMonday, currentMonday, nextMonday);
        Refresh();
    }

    public void NextWeek() {
        changeWeek(1);
    }

    public void PrevWeek() {
        changeWeek(-1);
    }

    public void Today() {
        currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        changeWeek(0);
    }

    public void Refresh() {
        events = getEvents(prevMonday, nextMonday.plusDays(6));
        drawEvents();
        drawTimeline();
    }
}
