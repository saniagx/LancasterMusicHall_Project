package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class UsageChart {
    // Chart attributes
    @FXML private TableView<String> venueTable;
    @FXML private TableColumn<String, String> venueColumn;
    @FXML private Canvas chartCanvas;
    @FXML private Canvas timelineCanvas;

    // Event Details Pane attributes
    @FXML private VBox eventDetails_VBox;
    @FXML private Label eventName_Label;
    @FXML private Label eventHost_Text;
    @FXML private Label eventStart_Text;
    @FXML private Label eventEnd_Text;
    @FXML private Label eventTicketSales_Label;
    @FXML private Label eventTicketSales_Text;

    private final List<String> venues = List.of(
            "Main Hall", "Small Hall", "Rehearsal Space"
    );
    private GraphicsContext chart_gc;
    private GraphicsContext timeline_gc;
    private final int cell_height = 150;
    private final int cell_width = 50;
    private LocalDate today;
    private LocalDate currentMonday;
    private LocalDate prevMonday;
    private LocalDate nextMonday;
    private List<LocalDate> weekStarts;
    private ArrayList<Event> events;
    private List<ClickableBar> clickableBars;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Map<Integer, Integer> venueCapacity = Map.of(
            0, 374, // Main Hall
            1, 95 // Small Hall
    );

    public UsageChart() {}

    @FXML
    public void initialize() {
        eventDetails_VBox.setVisible(false);
        // Setup venue list
        venueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        venueColumn.setMaxWidth(120);
        venueColumn.setPrefWidth(TableView.USE_COMPUTED_SIZE);
        venueColumn.setSortable(false);
        venueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        venueTable.setRowFactory(tv -> new TableRow<String>() { // Set row height
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setPrefHeight(cell_height);
            }
        });
        venueTable.getItems().addAll(venues);

        chart_gc = chartCanvas.getGraphicsContext2D();
        timeline_gc = timelineCanvas.getGraphicsContext2D();

        // Setting boundaries of the timeline
        today = LocalDate.now();
        currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        prevMonday = currentMonday.minusWeeks(1);
        nextMonday = currentMonday.plusWeeks(1);
        weekStarts = List.of(prevMonday, currentMonday, nextMonday);

        // Handle clicking of bars
        clickableBars = new ArrayList<>();
        chartCanvas.setOnMouseClicked(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            boolean found = false;
            for (ClickableBar bar : clickableBars) {
                if (bar.contains(mouseX, mouseY)) {
                    Event clickedEvent = bar.getEvent();
                    displayEventDetails(clickedEvent);
                    found = true;
                    break;
                }
            }

            // No bar clicked, clear details
            if (!found) {
                clearEventDetails();
            }
        });

        // Draw the screen
        Refresh();
    }

    // Draws background
    private void drawChartBase() {
        for (int i = 0; i < 22; i++) {
            if (i % 2 == 0) {
                chart_gc.setFill(Color.WHITE);
            } else {
                chart_gc.setFill(Color.LIGHTGRAY);
            }
            chart_gc.fillRect(cell_width * i, 0, cell_width, cell_height * 9);
        }

        chart_gc.setStroke(Color.BLACK);
        chart_gc.setLineWidth(1);
        for (int j = 0; j <= venues.size(); j++) {
            double y = cell_height * j;
            chart_gc.strokeLine(0, y, cell_height * 22, y);
        }
    }

    // Draws the timeline
    private void drawTimeline() {
        double dayWidth = cell_width;

        timeline_gc.setFill(Color.WHITE);
        timeline_gc.fillRect(0, 0, timelineCanvas.getWidth(), 39);
        for (int i = 0; i < 22; i++) {
            if (i % 2 == 0) {
                timeline_gc.setFill(Color.WHITE);
            } else {
                timeline_gc.setFill(Color.LIGHTGRAY);
            }
            timeline_gc.fillRect(cell_width*i, 39, cell_width, 24);
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
            timeline_gc.fillText(weekStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), weekX + 5, 34);

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

    // Draw bars for each event
    private void drawEvents() {
        clickableBars.clear();
        drawChartBase();

        // Group events by venue
        Map<Integer, List<Event>> eventsByVenue = new HashMap<>();
        for (int venueIndex = 0; venueIndex < venues.size(); venueIndex++) {
            eventsByVenue.put(venueIndex, new ArrayList<>());
        }
        for (Event event : events) {
            int venueIndex = venues.indexOf(event.getVenueName());
            if (venueIndex != -1) {
                eventsByVenue.get(venueIndex).add(event);
            }
        }

        // Process each venue
        for (int venueIndex = 0; venueIndex < venues.size(); venueIndex++) {
            List<Event> venueEvents = eventsByVenue.get(venueIndex);
            // Sort events by start date
            venueEvents.sort(Comparator.comparing(Event::getEventStart));

            // Track the base color for consecutive events
            boolean useDarkBlue = false; // Start with blue, then alternate

            for (int i = 0; i < venueEvents.size(); i++) {
                Event event = venueEvents.get(i);

                // Calculate y-position (center the bar in the cell)
                double y = venueIndex * cell_height + 37.5;
                double barHeight = cell_height - 75;
                double halfHeight = barHeight / 2;

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

                // Check if this event is upcoming
                LocalDateTime now = LocalDateTime.now();
                boolean isUpcoming = event.getEventStart().isAfter(now);

                // Determine base color
                Color baseColor;
                if (isUpcoming) {
                    baseColor = Color.LIGHTBLUE; // Upcoming events are light blue
                } else {
                    // Alternate between blue and dark blue
                    baseColor = useDarkBlue ? Color.DARKBLUE : Color.BLUE;
                    // Check if the next event is consecutive
                    if (i < venueEvents.size() - 1) {
                        Event nextEvent = venueEvents.get(i + 1);
                        LocalDate nextStartDate = nextEvent.getEventStart().toLocalDate();
                        if (eventEndDate.plusDays(1).equals(nextStartDate)) {
                            useDarkBlue = !useDarkBlue; // Alternate for consecutive events
                        } else {
                            useDarkBlue = false; // Reset if not consecutive
                        }
                    }
                }

                LocalDate currentDate = eventStartDate;
                while (!currentDate.isAfter(eventEndDate)) {
                    // Calculate x-position and width
                    long startOffset = ChronoUnit.DAYS.between(prevMonday, currentDate);
                    double x = startOffset * cell_width;
                    double width = cell_width;

                    // Get ticket sales for this day
                    int ticketsSold = event.getTicketsSoldForDay(currentDate);
                    boolean hasTicketSales = ticketsSold > -1;

                    Color topColor = baseColor; // Default to base color if no ticket sales
                    if (hasTicketSales && (venueIndex == 0 || venueIndex == 1)) { // Main Hall or Small Hall
                        double salesPercentage = ((double) ticketsSold / (double) venueCapacity.get(venueIndex)) * 100;
                        if (salesPercentage == 100.0) {
                            topColor = Color.MEDIUMPURPLE; // Sold out
                        } else if (salesPercentage >= 75.0) {
                            topColor = Color.LIGHTGREEN; // >= 75% sold
                        } else if (salesPercentage >= 50.0) {
                            topColor = Color.YELLOW; // >= 50% sold
                        } else {
                            topColor = Color.RED; // < 50% sold
                        }
                    }

                    // Draw the bar: bottom half (base color), top half (ticket sales color)
                    chart_gc.setFill(baseColor);
                    chart_gc.fillRect(x, y + halfHeight, width, halfHeight); // Bottom half
                    chart_gc.setFill(topColor);
                    chart_gc.fillRect(x, y, width, halfHeight); // Top half

                    clickableBars.add(new ClickableBar(x, y, width, barHeight, event));

                    currentDate = currentDate.plusDays(1);
                }

                long duration = ChronoUnit.DAYS.between(eventStartDate, eventEndDate) + 1;
                double totalBarWidth = duration * cell_width;

                long startOffset = ChronoUnit.DAYS.between(prevMonday, eventStartDate);
                double x = startOffset * cell_width;

                chart_gc.setFill(Color.WHITE);
                Font font = Font.font("System", FontWeight.BOLD, 12);
                chart_gc.setFont(font);
                String hostName = event.getEventHost();
                double textWidth = getTextWidth(hostName, font);

                if (textWidth > totalBarWidth + 5) {
                    int maxChars = (int) (totalBarWidth / 6) -3;
                    if (maxChars < 1) maxChars = 1;
                    hostName = hostName.substring(0, maxChars) + "...";
                }

                double textX = x + 5; // Padding
                double textY = y + halfHeight + (halfHeight / 2) + 5;
                chart_gc.fillText(hostName, textX, textY);
            }
        }
    }

    private double getTextWidth(String text, Font font) {
        Text tempText = new Text(text);
        tempText.setFont(font);
        return tempText.getBoundsInLocal().getWidth();
    }

    // Fetches all events for specific time frame
    public ArrayList<Event> getEvents(LocalDate start, LocalDate end) {
        ArrayList<Event> events = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return events;
            }
            // Fetch events
            String eventsQuery = "SELECT e.event_id, e.name, e.type, e.start, e.end, e.price, e.venue_id, e.client_id, e.tickets_sold, c.company_name AS client_name, v.name as venue_name " +
                    "FROM Events e " +
                    "JOIN Clients c ON e.client_id = c.client_id " +
                    "JOIN Venues v ON e.venue_id = v.venue_id " +
                    "WHERE e.start <= ? AND e.end >= ?";
            PreparedStatement eventsStmt = conn.prepareStatement(eventsQuery);
            eventsStmt.setString(1, end.plusDays(1).toString());
            eventsStmt.setString(2, start.minusDays(1).toString());
            ResultSet eventRs = eventsStmt.executeQuery();

            // Fetch daily ticket sales for each event
            String salesQuery = "SELECT event_date, tickets_sold FROM DailyTicketSales WHERE event_id = ?";
            PreparedStatement salesStmt = conn.prepareStatement(salesQuery);

            // Create event objects and add to array
            while (eventRs.next()) {
                int eventID = eventRs.getInt("event_id");
                String name = eventRs.getString("name");
                String type = eventRs.getString("type");
                String client = eventRs.getString("client_name");
                LocalDateTime startTimestamp = eventRs.getTimestamp("start").toLocalDateTime();
                LocalDateTime endTimestamp = eventRs.getTimestamp("end").toLocalDateTime();
                BigDecimal price = BigDecimal.valueOf(eventRs.getDouble("price"));
                int venueID = eventRs.getInt("venue_id");
                String venueName = eventRs.getString("venue_name");

                // Fetch daily ticket sales for this event
                Map<LocalDate, Integer> dailyTicketSales = new HashMap<>();
                salesStmt.setInt(1, eventID);
                ResultSet salesRs = salesStmt.executeQuery();
                while (salesRs.next()) {
                    LocalDate eventDate = salesRs.getDate("event_date").toLocalDate();
                    int ticketsSold = salesRs.getInt("tickets_sold");
                    dailyTicketSales.put(eventDate, ticketsSold);
                }
                salesRs.close();

                Event event = new Event(eventID, name, type, client, startTimestamp, endTimestamp, price, venueID, venueName, dailyTicketSales);
                events.add(event);
            }

            eventRs.close();
            eventsStmt.close();
            salesStmt.close();
        } catch (SQLException e) {
            System.out.println("Failed to fetch events from database" + e.getMessage());
            return events;
        }
        return events;
    }

    // Set text fields to the event's information
    private void displayEventDetails(Event event) {
        eventDetails_VBox.setVisible(true);
        eventName_Label.setText(event.getEventName());
        eventHost_Text.setText(event.getEventHost());
        eventStart_Text.setText(event.getEventStart().format(formatter));
        eventEnd_Text.setText(event.getEventEnd().format(formatter));
        if (event.getVenueID() == 2) {
            eventTicketSales_Label.setVisible(false);
            eventTicketSales_Text.setVisible(false);
        } else {
            eventTicketSales_Label.setVisible(true);
            eventTicketSales_Text.setVisible(true);

            int totalTicketsSold = event.getTotalTicketsSold();
            int totalTicketsCapacity = venueCapacity.get(event.getVenueID()) * event.getDaysWithTicketSales();

            double salesPercentage = ((double) totalTicketsSold / totalTicketsCapacity)*100;
            eventTicketSales_Text.setText(totalTicketsSold + " / " + totalTicketsCapacity + "\t\t" + String.format("%.2f", salesPercentage) + "%");
        }
    }

    // Hide and reset event detail fields
    private void clearEventDetails() {
        eventDetails_VBox.setVisible(false);
        eventName_Label.setText("");
        eventHost_Text.setText("");
        eventStart_Text.setText("");
        eventEnd_Text.setText("");
        eventTicketSales_Text.setText("");
    }

    // Adjust boundaries of timeline
    public void changeWeek(int weeks) {
        currentMonday = currentMonday.plusWeeks(weeks);
        prevMonday = currentMonday.minusWeeks(1);
        nextMonday = currentMonday.plusWeeks(1);
        weekStarts = List.of(prevMonday, currentMonday, nextMonday);
        Refresh();
    }

    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
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

    private static class ClickableBar {
        private final double x;
        private final double y;
        private final double width;
        private final double height;
        private final Event event;

        public ClickableBar(double x, double y, double width, double height, Event event) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.event = event;
        }

        public boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
        }

        public Event getEvent() {
            return event;
        }
    }
}
