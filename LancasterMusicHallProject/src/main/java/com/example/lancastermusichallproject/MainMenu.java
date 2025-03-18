package com.example.lancastermusichallproject;

import com.example.lancastermusichallproject.database.DatabaseConnection;

public class MainMenu extends Application {

    public MainMenu() {}
    public void UsageChart() {
        ScreenController.loadScreen("UsageChart");
    }
    public void Diary() {
        ScreenController.loadScreen("Diary");
    }
    public void Logout() {
        DatabaseConnection.closeConnection();
        ScreenController.loadScreen("Login");
    }
    public void DailySheet() {
        ScreenController.loadScreen("DailySheet");
    }
}
