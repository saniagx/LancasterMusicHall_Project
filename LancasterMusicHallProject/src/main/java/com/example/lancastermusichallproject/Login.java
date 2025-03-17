package com.example.lancastermusichallproject;

public class Login extends Application{

    public Login(){}


    //Login button calls mainMenuScreen within Application
    public void LoginButton() {
        // To do: Compare entered fields for username and password with database values
        ScreenController.loadScreen("MainMenu");
    }

}
