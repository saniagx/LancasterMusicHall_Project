package com.venueOps.lancastermusichallproject;

import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import com.venueOps.lancastermusichallproject.operations.*;

/**
 * Screen Controller class for the Main Menu screen
 * Shows all buttons linking to other key parts of the application
 * @author Neil Daya
 * @author Meer Ali
 * @author Sania Ghori
 * @version 8.0 April 7 2025
 */
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
        Invoices invoicesController = (Invoices) ScreenController.getController("Invoices");
        if (invoicesController != null) {
            invoicesController.refreshInvoices();
        }
        ScreenController.loadScreen("Invoices");
    }
    public void Contracts() {
        Contracts contractsController = (Contracts) ScreenController.getController("Contracts");
        if (contractsController != null) {
            contractsController.refreshContracts();
        }
        ScreenController.loadScreen("Contracts");
    }

    public void DailySheet () {
        DailySheet dailySheetController = (DailySheet) ScreenController.getController("DailySheet");
        if (dailySheetController != null) {
            dailySheetController.Refresh();
        }
        ScreenController.loadScreen("DailySheet");
    }

    public void Reviews() {
        Reviews Reviewscontroller = (Reviews) ScreenController.getController("Reviews");
        if (Reviewscontroller != null) {
            Reviewscontroller.Refresh();
        }
        ScreenController.loadScreen("Reviews");
    }

    public void Logout() {
        DatabaseConnection.closeConnection();
        ScreenController.loadScreen("Login");
    }

}
