
package com.example.lancastermusichallproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
public class diaryController {


    //Back to Main Menu calls mainMenuScreen within Application
    @FXML
    void backMainMenu(ActionEvent event) throws IOException {
        Application.mainMenuScreen();
    }

}
