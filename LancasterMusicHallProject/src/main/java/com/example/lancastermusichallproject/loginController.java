package com.example.lancastermusichallproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class loginController {


    //Login button calls mainMenuScreen within Application
    @FXML
    void LoginButton(ActionEvent event) throws IOException {
        Application.mainMenuScreen();
    }

}
