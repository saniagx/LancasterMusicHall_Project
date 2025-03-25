package com.venueOps.lancastermusichallproject;

import com.venueOps.lancastermusichallproject.operations.Event;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DailySheet {

    // Attributes for 'Used' text objects, unused rooms have strikethrough applied to them
    @FXML Text mainHall_usedText;
    @FXML Text smallHall_usedText;
    @FXML Text rehearsalSpace_usedText;
    @FXML Text theGreenRoom_usedText;
    @FXML Text bronteBoardroom_usedText;
    @FXML Text dickensDen_usedText;
    @FXML Text poeParlor_usedText;
    @FXML Text globeRoom_usedText;
    @FXML Text chekhovChamber_usedText;

    // Attributes for grid objects, to be disabled if the corresponding 'Used' attribute is false
    @FXML GridPane mainHall_grid;
    @FXML GridPane smallHall_grid;
    @FXML GridPane rehearsalSpace_grid;

    // Attributes for individual fields in the grids, to be set based on the events held
    @FXML TextField mainHall_who;
    @FXML TextField mainHall_startTime;
    @FXML TextField mainHall_endTime;
    @FXML TextField mainHall_seatingConfig;
    @FXML TextField smallHall_who;
    @FXML TextField smallHall_startTime;
    @FXML TextField smallHall_endTime;
    @FXML TextField smallHall_seatingConfig;
    @FXML TextField rehearsalSpace_who;
    @FXML TextField rehearsalSpace_startTime;
    @FXML TextField rehearsalSpace_endTime;
    @FXML TextField rehearsalSpace_seatingConfig;

    // Attributes for the meeting room buttons, to be disabled if corresponding 'Used' attribute is false
    @FXML Button theGreenRoom_button;
    @FXML Button bronteBoardroom_button;
    @FXML Button dickensDen_button;
    @FXML Button poeParlor_button;
    @FXML Button globeRoom_button;
    @FXML Button chekhovChamber_button;

    // Attributes for meeting room pop up pane, invisible on initialisation but visible when corresponding button clicked
    @FXML Pane meetingRoom_pane;
    @FXML Text meetingPane_nameText;
    @FXML VBox meetingPane_VBox;
    @FXML ScrollPane meetingPane_ScrollPane;
    @FXML Button meetingPane_CloseButton;

    private LocalDateTime date;
    private ArrayList<Event> events;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DailySheet() {}

    @FXML private void initialize() throws Exception {
        date = LocalDateTime.now(); // TEMPORARY - THIS SHOULD BE SET TO THE DATE SELECTED ON THE CALENDAR
        disableByDefault();
        meetingRoom_pane.setVisible(false);
        events = getEvents();
        initialiseEvents();
    }

    public void setDate(LocalDateTime date) { this.date = date; }
    public LocalDateTime getDate() { return date; }

    // By default, assumes all venues are unused
    public void disableByDefault() {
        // Used texts
        mainHall_usedText.setStrikethrough(true);
        smallHall_usedText.setStrikethrough(true);
        rehearsalSpace_usedText.setStrikethrough(true);
        theGreenRoom_usedText.setStrikethrough(true);
        bronteBoardroom_usedText.setStrikethrough(true);
        dickensDen_usedText.setStrikethrough(true);
        poeParlor_usedText.setStrikethrough(true);
        globeRoom_usedText.setStrikethrough(true);
        chekhovChamber_usedText.setStrikethrough(true);
        // Grid panes
        mainHall_grid.setDisable(true);
        smallHall_grid.setDisable(true);
        rehearsalSpace_grid.setDisable(true);
        // Expand detail buttons
        theGreenRoom_button.setDisable(true);
        bronteBoardroom_button.setDisable(true);
        dickensDen_button.setDisable(true);
        poeParlor_button.setDisable(true);
        globeRoom_button.setDisable(true);
        chekhovChamber_button.setDisable(true);
    }

    // Fetches all events for specific day
    public ArrayList<Event> getEvents() {
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
                    "WHERE DATE(e.start) <= ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, date.toLocalDate().toString()); // Set day for query to given day
            ResultSet rs = pstmt.executeQuery();

            // Create event objects and add to array
            while (rs.next()) {
                int eventID = rs.getInt("event_id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String host = rs.getString("host_name");
                LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();
                BigDecimal price = BigDecimal.valueOf(rs.getDouble("price"));
                int venueID = rs.getInt("venue_id");
                String venueName = rs.getString("venue_name");
                int ticketsSold = rs.getInt("tickets_sold");

                Event event = new Event(eventID, name, type, host, start, end, price, venueID, venueName, ticketsSold);
                events.add(event);
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println("Failed to fetch events from database" + e.getMessage());
            return events;
        }
        return events;
    }

    // Iterates through events and fills in information for each venue
    public void initialiseEvents() throws Exception {
        for (Event event : events) {
            switch (event.getVenueID()) {
                case 0: // Main Hall
                    mainHall_usedText.setStrikethrough(false);
                    mainHall_grid.setDisable(false);
                    mainHall_who.setText(event.getEventHost());
                    mainHall_startTime.setText(event.getEventStart().format(formatter));
                    mainHall_endTime.setText(event.getEventEnd().format(formatter));
                    mainHall_seatingConfig.setText(String.valueOf(event.getSeatingConfigID())); // This should probably be replaced with the name of a seating config
                    break;
                case 1: // Small Hall
                    smallHall_usedText.setStrikethrough(false);
                    smallHall_grid.setDisable(false);
                    smallHall_who.setText(event.getEventHost());
                    smallHall_startTime.setText(event.getEventStart().format(formatter));
                    smallHall_endTime.setText(event.getEventEnd().format(formatter));
                    smallHall_seatingConfig.setText(String.valueOf(event.getSeatingConfigID()));
                    break;
                case 2: // Rehearsal Space
                    rehearsalSpace_usedText.setStrikethrough(false);
                    rehearsalSpace_grid.setDisable(false);
                    rehearsalSpace_who.setText(event.getEventHost());
                    rehearsalSpace_startTime.setText(event.getEventStart().format(formatter));
                    rehearsalSpace_endTime.setText(event.getEventEnd().format(formatter));
                    rehearsalSpace_seatingConfig.setText(String.valueOf(event.getSeatingConfigID()));
                    break;
                case 3: // The Green Room
                    theGreenRoom_usedText.setStrikethrough(false);
                    theGreenRoom_button.setDisable(false);
                    break;
                case 4: // Bronte Boardroom
                    bronteBoardroom_usedText.setStrikethrough(false);
                    bronteBoardroom_button.setDisable(false);
                    break;
                case 5: // Dickens Den
                    dickensDen_usedText.setStrikethrough(false);
                    dickensDen_button.setDisable(false);
                    break;
                case 6: // Poe Parlor
                    poeParlor_usedText.setStrikethrough(false);
                    poeParlor_button.setDisable(false);
                    break;
                case 7: // Globe Room
                    globeRoom_usedText.setStrikethrough(false);
                    globeRoom_button.setDisable(false);
                    break;
                case 8: // Chekhov Chamber
                    chekhovChamber_usedText.setStrikethrough(false);
                    chekhovChamber_button.setDisable(false);
                    break;
                default:
                    throw new Exception("Invalid event ID for " + event.getEventName());
            }
        }
    }

    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

    public void OpenMeetingRoomPane(String venue) {
        meetingRoom_pane.getChildren().clear();

        meetingRoom_pane.setVisible(true);
        meetingRoom_pane.getChildren().add(meetingPane_CloseButton);
        meetingRoom_pane.getChildren().add(meetingPane_nameText);
        meetingRoom_pane.getChildren().add(meetingPane_ScrollPane);
        meetingPane_nameText.setText(venue);

        meetingPane_ScrollPane.setFitToWidth(true);
        meetingPane_ScrollPane.setFitToHeight(false);
        meetingPane_VBox.setMaxHeight(Double.MAX_VALUE);

        List<Event> meetingList = getMeetingEventsForVenue(venue);

        int meetingCounter = 1;
        for (Event meeting : meetingList) {
            if (meeting.getEventStart().toLocalDate().equals(date.toLocalDate())) {
                Label meetingLabel = new Label("Meeting " + meetingCounter);
                meetingLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-padding: 5 0 5 0;");
                GridPane meetingGrid = createMeetingGridPane(meeting);
                meetingPane_VBox.getChildren().add(meetingLabel);
                meetingPane_VBox.getChildren().add(meetingGrid);

                meetingCounter++;
            }
        }
    }

    public void CloseMeetingPane() {
        meetingRoom_pane.setVisible(false);
        meetingPane_VBox.getChildren().clear();
    }

    public void TheGreenRoom_ExpandDetails() {
        CloseMeetingPane();
        OpenMeetingRoomPane("The Green Room");
    }

    public void BronteBoardroom_ExpandDetails() {
        CloseMeetingPane();
        OpenMeetingRoomPane("Bronte Boardroom");
    }

    public void DickensDen_ExpandDetails() {
        CloseMeetingPane();
        OpenMeetingRoomPane("Dickens Den");
    }

    public void PoeParlor_ExpandDetails() {
        CloseMeetingPane();
        OpenMeetingRoomPane("Poe Parlor");
    }

    public void GlobeRoom_ExpandDetails() {
        CloseMeetingPane();
        OpenMeetingRoomPane("Globe Room");
    }

    public void ChekhovChamber_ExpandDetails() {
        CloseMeetingPane();
        OpenMeetingRoomPane("Chekhov Chamber");
    }

    private List<Event> getMeetingEventsForVenue(String venue) {
        return events.stream()
                .filter(event -> event.getEventType().equals("Meeting") && event.getVenueName().equals(venue))
                .collect(Collectors.toList());
    }

    private GridPane createMeetingGridPane(Event event) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        // Meeting Details
        Label whoLabel = new Label("Who:");
        TextField whoField = new TextField(event.getEventHost());
        Label startLabel = new Label("Start:");
        TextField startField = new TextField(event.getEventStart().format(formatter));
        Label endLabel = new Label("End:");
        TextField endField = new TextField(event.getEventEnd().format(formatter));

        // Add labels to the grid
        gridPane.add(whoLabel, 0, 0);
        gridPane.add(whoField, 1, 0);
        gridPane.add(startLabel, 0, 1);
        gridPane.add(startField, 1, 1);
        gridPane.add(endLabel, 0, 2);
        gridPane.add(endField, 1, 2);

        return gridPane;
    }
}
