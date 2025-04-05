package com.venueOps.lancastermusichallproject;

import com.venueOps.lancastermusichallproject.database.DatabaseConnection;

public class MainMenu {

    public MainMenu() {}

    public void Calendar() { ScreenController.loadScreen("Calendar"); }

    public void UsageChart() {
        ScreenController.loadScreen("UsageChart");
    }

    public void Logout() {
        DatabaseConnection.closeConnection();
        ScreenController.loadScreen("Login");
    }

    public void DailySheet() {
        ScreenController.loadScreen("DailySheet");
    }
}
