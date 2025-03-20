package com.venueOps.lancastermusichallproject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

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

    public DailySheet() {}

    @FXML private void initialize() {
        meetingRoom_pane.setVisible(false);
    }

    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

    public void OpenMeetingRoomPane(String venue) {
        meetingRoom_pane.setVisible(true);
        meetingPane_nameText.setText(venue);
        // Needs logic to fetch all meeting events for the day
        // For each event, add a new grid to the pane containing details like Who, StartTime, EndTime
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
