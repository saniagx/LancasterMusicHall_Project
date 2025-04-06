package com.venueOps.lancastermusichallproject;

import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import com.venueOps.lancastermusichallproject.operations.Calendar;
import com.venueOps.lancastermusichallproject.operations.DailySheet;
import com.venueOps.lancastermusichallproject.operations.UsageChart;

public class MainMenu {

    public MainMenu() {}

    public void Calendar() {
        Calendar calendarController = (Calendar) ScreenController.getController("Calendar");
        if (calendarController != null) {
            calendarController.refreshCalendar();
        }
        ScreenController.loadScreen("Calendar"); }

    public void UsageChart() {
        UsageChart usageChartController = (UsageChart) ScreenController.getController("UsageChart");
        if (usageChartController != null) {
            usageChartController.Refresh();
        }
        ScreenController.loadScreen("UsageChart");
    }

    public void Invoices() {
        ScreenController.loadScreen("Invoices");
    }
    public void Contracts() {
        ScreenController.loadScreen("Contracts");
    }

    public void DailySheet () {
        DailySheet dailySheetController = (DailySheet) ScreenController.getController("DailySheet");
        if (dailySheetController != null) {
            dailySheetController.Refresh();
        }
        ScreenController.loadScreen("DailySheet");
    }

    public void Logout() {
        DatabaseConnection.closeConnection();
        ScreenController.loadScreen("Login");
    }

}
