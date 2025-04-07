package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;

public class DailySheet {
    // Attributes for grid objects, to be disabled if the corresponding 'Used' attribute is false
    @FXML GridPane mainHall_grid;
    @FXML GridPane smallHall_grid;
    @FXML GridPane rehearsalSpace_grid;

    // Attributes for individual fields in the grids, to be set based on the events held
    @FXML Label mainHall_who;
    @FXML Label mainHall_startTime;
    @FXML Label mainHall_endTime;
    @FXML Label mainHall_seatingConfig;
    @FXML Label smallHall_who;
    @FXML Label smallHall_startTime;
    @FXML Label smallHall_endTime;
    @FXML Label smallHall_seatingConfig;
    @FXML Label rehearsalSpace_who;
    @FXML Label rehearsalSpace_startTime;
    @FXML Label rehearsalSpace_endTime;
    @FXML Label rehearsalSpace_seatingConfig;

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
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public DailySheet() {}

    @FXML private void initialize() throws Exception {
        date = LocalDateTime.now();
        disableByDefault();
        meetingRoom_pane.setVisible(false);
        events = DatabaseConnection.getEventsForDailySheet(date);
        Refresh();
        initialiseEvents();
    }

    public void setDate(LocalDateTime date) { this.date = date; }
    public LocalDateTime getDate() { return date; }

    // By default, assumes all venues are unused
    public void disableByDefault() {
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

    // Iterates through events and fills in information for each venue
    public void initialiseEvents() throws Exception {
        for (Event event : events) {
            switch (event.getVenueID()) {
                case 0: // Main Hall
                    mainHall_grid.setDisable(false);
                    mainHall_who.setText(event.getEventHost());
                    mainHall_startTime.setText(event.getEventStart().format(formatter));
                    mainHall_endTime.setText(event.getEventEnd().format(formatter));
                    mainHall_seatingConfig.setText(String.valueOf(event.getSeatingConfig().getLayout())); // This should probably be replaced with the name of a seating config
                    break;
                case 1: // Small Hall
                    smallHall_grid.setDisable(false);
                    smallHall_who.setText(event.getEventHost());
                    smallHall_startTime.setText(event.getEventStart().format(formatter));
                    smallHall_endTime.setText(event.getEventEnd().format(formatter));
                    smallHall_seatingConfig.setText(String.valueOf(event.getSeatingConfig().getLayout()));
                    break;
                case 2: // Rehearsal Space
                    rehearsalSpace_grid.setDisable(false);
                    rehearsalSpace_who.setText(event.getEventHost());
                    rehearsalSpace_startTime.setText(event.getEventStart().format(formatter));
                    rehearsalSpace_endTime.setText(event.getEventEnd().format(formatter));
                    rehearsalSpace_seatingConfig.setText(String.valueOf(event.getSeatingConfig().getLayout()));
                    break;
                case 3: // The Green Room
                    theGreenRoom_button.setDisable(false);
                    break;
                case 4: // Bronte Boardroom
                    bronteBoardroom_button.setDisable(false);
                    break;
                case 5: // Dickens Den
                    dickensDen_button.setDisable(false);
                    break;
                case 6: // Poe Parlor
                    poeParlor_button.setDisable(false);
                    break;
                case 7: // Globe Room
                    globeRoom_button.setDisable(false);
                    break;
                case 8: // Chekhov Chamber
                    chekhovChamber_button.setDisable(false);
                    break;
                default:
                    throw new Exception("Invalid venue ID for " + event.getEventName());
            }
        }
    }

    public void BackButton() {
        CloseMeetingPane();
        ScreenController.loadScreen("MainMenu");
    }

    public void Export() {
        try {
            // Create directory if it doesn't exist
            String directoryPath = "DailySheets";
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
                System.out.println("Created directory: " + directory.getAbsolutePath());
            }

            String fileName = directoryPath + "/DailySheet_" + date.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".pdf";
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Title
            document.add(new Paragraph("Daily Sheet for " + date.toLocalDate().toString())
                    .setFontSize(16)
                    .setBold());

            // Section for Main Hall, Small Hall, and Rehearsal Space
            for (Event event : events) {
                int venueID = event.getVenueID();
                if (venueID == 0 || venueID == 1 || venueID == 2) {
                    String venueName = event.getVenueName();
                    document.add(new Paragraph(venueName)
                            .setFontSize(14)
                            .setBold()
                            .setMarginTop(10));

                    // Create a table for the event details
                    Table table = new Table(UnitValue.createPercentArray(new float[]{20, 80}));
                    table.setWidth(UnitValue.createPercentValue(100));

                    table.addCell(new Cell().add(new Paragraph("Event Name")));
                    table.addCell(new Cell().add(new Paragraph(event.getEventName())));
                    table.addCell(new Cell().add(new Paragraph("Host")));
                    table.addCell(new Cell().add(new Paragraph(event.getEventHost())));
                    table.addCell(new Cell().add(new Paragraph("Start Time")));
                    table.addCell(new Cell().add(new Paragraph(event.getEventStart().format(formatter))));
                    table.addCell(new Cell().add(new Paragraph("End Time")));
                    table.addCell(new Cell().add(new Paragraph(event.getEventEnd().format(formatter))));
                    table.addCell(new Cell().add(new Paragraph("Seating Config")));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(event.getSeatingConfig().getLayout()))));

                    document.add(table);
                }
            }

            // Section for Meeting Rooms
            List<String> meetingRooms = List.of("The Green Room", "Bronte Boardroom", "Dickens Den", "Poe Parlor", "Globe Room", "Chekhov Chamber");
            for (String venue : meetingRooms) {
                List<Event> meetings = getMeetingEventsForVenue(venue);
                if (!meetings.isEmpty()) {
                    document.add(new Paragraph(venue)
                            .setFontSize(14)
                            .setBold()
                            .setMarginTop(10));

                    int meetingCounter = 1;
                    for (Event meeting : meetings) {
                        if (meeting.getEventStart().toLocalDate().equals(date.toLocalDate())) {
                            document.add(new Paragraph("Meeting " + meetingCounter)
                                    .setFontSize(12)
                                    .setBold());

                            Table table = new Table(UnitValue.createPercentArray(new float[]{20, 80}));
                            table.setWidth(UnitValue.createPercentValue(100));

                            table.addCell(new Cell().add(new Paragraph("Host")));
                            table.addCell(new Cell().add(new Paragraph(meeting.getEventHost())));
                            table.addCell(new Cell().add(new Paragraph("Start Time")));
                            table.addCell(new Cell().add(new Paragraph(meeting.getEventStart().format(formatter))));
                            table.addCell(new Cell().add(new Paragraph("End Time")));
                            table.addCell(new Cell().add(new Paragraph(meeting.getEventEnd().format(formatter))));
                            table.addCell(new Cell().add(new Paragraph("Seating Config")));
                            table.addCell(new Cell().add(new Paragraph(String.valueOf(meeting.getSeatingConfig().getLayout()))));

                            document.add(table);
                            meetingCounter++;
                        }
                    }
                }
            }

            document.close();
            System.out.println("PDF exported successfully to " + fileName);

        } catch (Exception e) {
            System.err.println("Error exporting to PDF: " + e.getMessage());
        }
    }

    // Display meeting room pane and fill the screen with the list of meetings that occurred in that room
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
        meetingPane_VBox.setAlignment(Pos.CENTER_LEFT);

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

    // Fetch list of meetings for given venue
    private List<Event> getMeetingEventsForVenue(String venue) {
        return events.stream()
                .filter(event -> event.getEventType().equals("Meeting") && event.getVenueName().equals(venue))
                .collect(Collectors.toList());
    }

    // Details of meetings in the meeting pane are shown as a grid
    private GridPane createMeetingGridPane(Event event) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER_LEFT);

        // Meeting Details
        Label whoLabel = new Label("Who:");
        Label whoField = new Label(event.getEventHost());
        Label startLabel = new Label("Start:");
        Label startField = new Label(event.getEventStart().format(formatter));
        Label endLabel = new Label("End:");
        Label endField = new Label(event.getEventEnd().format(formatter));
        Label seatingLabel = new Label("Seating Config:");
        Label seatingField = new Label(String.valueOf(event.getSeatingConfig().getLayout()));

        // Add labels to the grid
        gridPane.add(whoLabel, 0, 0);
        gridPane.add(whoField, 1, 0);
        gridPane.add(startLabel, 0, 1);
        gridPane.add(startField, 1, 1);
        gridPane.add(endLabel, 0, 2);
        gridPane.add(endField, 1, 2);
        gridPane.add(seatingLabel, 0, 3);
        gridPane.add(seatingField, 1, 3);

        return gridPane;
    }

    public void Refresh() {
        events = DatabaseConnection.getEventsForDailySheet(date);
    }
}
