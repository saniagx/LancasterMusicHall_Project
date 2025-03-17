package com.example.lancastermusichallproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class mainMenuController {

    //button for diary which calls
    @FXML
    void diaryButton(ActionEvent event) throws IOException {
        Application.diaryScreen();
    }

    //button for logging out which calls the
    @FXML
    void logoutButton(ActionEvent event) throws IOException {
        Application.loginScreen();
    }

    //button for usage chart which calls
    @FXML
    void usageChartButton(ActionEvent event) throws IOException {
        Application.usageChartScreen();
    }

    //button for daily sheet which calls
    @FXML
    void dailySheetButton(ActionEvent event) throws IOException {
        Application.dailySheetScreen();
    }



}
