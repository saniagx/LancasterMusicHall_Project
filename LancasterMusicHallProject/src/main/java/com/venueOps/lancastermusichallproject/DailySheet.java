package com.venueOps.lancastermusichallproject;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class DailySheet {

    private class DailySheetController {
        @FXML private TextField mainHallWho;
        @FXML private TextField mainHallStart;
        @FXML private TextField mainHallEnd;
        @FXML private TextField mainHallSC;

        @FXML private TextField smallHallWho;
        @FXML private TextField smallHallStart;
        @FXML private TextField smallHallEnd;
        @FXML private TextField smallHallSC;

        @FXML private TextField rehearsalSpaceWho;
        @FXML private TextField rehearsalSpaceStart;
        @FXML private TextField rehearsalSpaceEnd;
        @FXML private TextField rehearsalSpaceSC;

        private void textHandler() {
            String mainHallW = mainHallEnd.getText();
            String mainHallS = mainHallStart.getText();
            String mainHallE = mainHallEnd.getText();
            String mainHallC = mainHallSC.getText();

            System.out.println("Main Hall - Who: " + mainHallW + ", Start Time: " + mainHallS +
                    ", End Time: " + mainHallE + ", Seating Configuration: " + mainHallC);

            String smallHallW = smallHallWho.getText();
            String smallHallS = smallHallStart.getText();
            String smallHallE = smallHallEnd.getText();
            String smallHallC = smallHallSC.getText();

            System.out.println("Small Hall - Who: " + smallHallW + ", Start Time: " + smallHallS +
                    ", End Time: " + smallHallE + ", Seating Configuration: " + smallHallC);

            String rehearsalSpaceW = rehearsalSpaceWho.getText();
            String rehearsalSpaceS = rehearsalSpaceStart.getText();
            String rehearsalSpaceE = rehearsalSpaceEnd.getText();
            String rehearsalSpaceC = rehearsalSpaceSC.getText();

            System.out.println("Rehearsal Space - Who: " + rehearsalSpaceW + ", Start Time: " + rehearsalSpaceS +
                    ", End Time: " + rehearsalSpaceE + ", Seating Configuration: " + rehearsalSpaceC);
        }

    }

    public DailySheet() {}

    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }
}
