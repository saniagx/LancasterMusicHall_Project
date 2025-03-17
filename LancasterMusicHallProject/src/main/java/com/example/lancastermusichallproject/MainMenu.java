package com.example.lancastermusichallproject;

public class MainMenu extends Application {

    public MainMenu() {}
    public void UsageChart() {
        ScreenController.loadScreen("UsageChart");
    }
    public void Diary() {
        ScreenController.loadScreen("Diary");
    }
    public void Logout() {
        ScreenController.loadScreen("Login");
    }



}
