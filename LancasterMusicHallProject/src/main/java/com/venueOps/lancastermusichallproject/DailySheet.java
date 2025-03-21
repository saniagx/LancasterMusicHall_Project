package com.venueOps.lancastermusichallproject;

import com.venueOps.lancastermusichallproject.operations.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.ArrayList;

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

    private LocalDate date;
    private ArrayList<Event> events;

    public DailySheet() {}

    @FXML private void initialize() throws Exception {
        disableByDefault();
        meetingRoom_pane.setVisible(false);
        events = getEvents();
        initialiseEvents();
    }

    public void setDate(LocalDate date) { this.date = date; }
    public LocalDate getDate() { return date; }

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

    public ArrayList<Event> getEvents() {
        // Fetch all events for specific day
        return new ArrayList<>();
    }

    // Iterates through events and fills in information for each venue
    public void initialiseEvents() throws Exception {
        for (Event event : events) {
            switch (event.getVenueID()) {
                case 0: // Main Hall
                    mainHall_usedText.setStrikethrough(false);
                    mainHall_grid.setDisable(false);
                    mainHall_who.setText(event.getEventHost());
                    mainHall_startTime.setText(event.getEventStart().toString());
                    mainHall_endTime.setText(event.getEventEnd().toString());
                    mainHall_seatingConfig.setText(String.valueOf(event.getSeatingConfigID())); // This should probably be replaced with the name of a seating config
                    break;
                case 1: // Small Hall
                    smallHall_usedText.setStrikethrough(false);
                    smallHall_grid.setDisable(false);
                    smallHall_who.setText(event.getEventHost());
                    smallHall_startTime.setText(event.getEventStart().toString());
                    smallHall_endTime.setText(event.getEventEnd().toString());
                    smallHall_seatingConfig.setText(String.valueOf(event.getSeatingConfigID()));
                    break;
                case 2: // Rehearsal Space
                    rehearsalSpace_usedText.setStrikethrough(false);
                    rehearsalSpace_grid.setDisable(false);
                    rehearsalSpace_who.setText(event.getEventHost());
                    rehearsalSpace_startTime.setText(event.getEventStart().toString());
                    rehearsalSpace_endTime.setText(event.getEventEnd().toString());
                    rehearsalSpace_seatingConfig.setText(String.valueOf(event.getSeatingConfigID()));
                    break;
                case 3: // The Green Room
                    theGreenRoom_usedText.setStrikethrough(false);
                    theGreenRoom_button.setDisable(false);
                    // LOGIC TO FILL EXPAND DETAILS WITH MEETING EVENTS
                    break;
                case 4: // Bronte Boardroom
                    bronteBoardroom_usedText.setStrikethrough(false);
                    bronteBoardroom_button.setDisable(false);
                    // LOGIC TO FILL EXPAND DETAILS WITH MEETING EVENTS
                    break;
                case 5: // Dickens Den
                    dickensDen_usedText.setStrikethrough(false);
                    dickensDen_button.setDisable(false);
                    // LOGIC TO FILL EXPAND DETAILS WITH MEETING EVENTS
                    break;
                case 6: // Poe Parlor
                    poeParlor_usedText.setStrikethrough(false);
                    poeParlor_button.setDisable(false);
                    // LOGIC TO FILL EXPAND DETAILS WITH MEETING EVENTS
                    break;
                case 7: // Globe Room
                    globeRoom_usedText.setStrikethrough(false);
                    globeRoom_button.setDisable(false);
                    // LOGIC TO FILL EXPAND DETAILS WITH MEETING EVENTS
                    break;
                case 8: // Chekhov Chamber
                    chekhovChamber_usedText.setStrikethrough(false);
                    chekhovChamber_button.setDisable(false);
                    // LOGIC TO FILL EXPAND DETAILS WITH MEETING EVENTS
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
        meetingRoom_pane.setVisible(true);
        meetingPane_nameText.setText(venue);
        // For each meeting event, add a new grid to the pane containing details like Who, StartTime, EndTime
        // (no SeatingConfig needed as meeting rooms don't have seating configs)
    }

    public void CloseMeetingPane() {
        meetingRoom_pane.setVisible(false);
    }

    public void TheGreenRoom_ExpandDetails() {
        OpenMeetingRoomPane("The Green Room");
    }

    public void BronteBoardroom_ExpandDetails() {
        OpenMeetingRoomPane("Brontë Boardroom");
    }

    public void DickensDen_ExpandDetails() {
        OpenMeetingRoomPane("Dickens Den");
    }

    public void PoeParlor_ExpandDetails() {
        OpenMeetingRoomPane("Poe Parlor");
    }

    public void GlobeRoom_ExpandDetails() {
        OpenMeetingRoomPane("Globe Room");
    }

    public void ChekhovChamber_ExpandDetails() {
        OpenMeetingRoomPane("Chekhov Chamber");
    }
}
