package com.venueOps.lancastermusichallproject;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;

public class Application extends javafx.application.Application {
    static ScreenController sc;

    // Attributes for debugging
    private double startDelay = 1.5; // Adjust this if it's too slow
    private boolean skipStartup = true; // Or set this to true to skip the startup entirely

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws IOException {
        try {
            Scene scene = new Scene(new AnchorPane(), 1280, 720);
            sc = new ScreenController(scene);
            initializeScreens();
            ScreenController.loadScreen("Startup"); // Startup screen is loaded on startup
            scene.getStylesheets().add(getClass().getResource("css.css").toExternalForm());

            URL imageUrl = getClass().getResource("assets/LMHLogo.png");
            Image LMHlogo = new Image(imageUrl.toExternalForm());
            stage.getIcons().add(LMHlogo);

            stage.setTitle("Lancaster's Music Hall");
            stage.setResizable(false);

            stage.setScene(scene);
            //stage.centerOnScreen();
            stage.show();

            if (skipStartup) {
                ScreenController.loadScreen("Login");
            } else {
                PauseTransition delay = new PauseTransition(Duration.seconds(startDelay));
                delay.setOnFinished(event -> ScreenController.loadScreen("Login"));
                delay.play();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeScreens() throws IOException {
        // Loaders for screens that require data to be passed between other screens
        FXMLLoader calendarLoader = new FXMLLoader(getClass().getResource("calendar.fxml"));
        sc.addScreen("Calendar", calendarLoader.load(), calendarLoader);
        FXMLLoader diaryLoader = new FXMLLoader(getClass().getResource("diary.fxml"));
        sc.addScreen("Diary", diaryLoader.load(), diaryLoader);
        FXMLLoader invoiceLoader = new FXMLLoader(getClass().getResource("invoices.fxml"));
        sc.addScreen("Invoices", invoiceLoader.load(), invoiceLoader);
        FXMLLoader invoicePageLoader = new FXMLLoader(getClass().getResource("invoicePage.fxml"));
        sc.addScreen("InvoicePage", invoicePageLoader.load(), invoicePageLoader);
        FXMLLoader contractsLoader = new FXMLLoader(getClass().getResource("contracts.fxml"));
        sc.addScreen("Contracts", contractsLoader.load(), contractsLoader);
        FXMLLoader contractPageLoader = new FXMLLoader(getClass().getResource("contractPage.fxml"));
        sc.addScreen("ContractPage", contractPageLoader.load(), contractPageLoader);
        FXMLLoader newBookingLoader = new FXMLLoader(getClass().getResource("newBooking.fxml"));
        sc.addScreen("NewBooking", newBookingLoader.load(), newBookingLoader);
        FXMLLoader bookingsOverviewLoader = new FXMLLoader(getClass().getResource("bookingsOverview.fxml"));
        sc.addScreen("BookingsOverview", bookingsOverviewLoader.load(), bookingsOverviewLoader);
        FXMLLoader usageChartLoader = new FXMLLoader(getClass().getResource("usageChart.fxml"));
        sc.addScreen("UsageChart", usageChartLoader.load(), usageChartLoader);
        FXMLLoader dailySheetLoader = new FXMLLoader(getClass().getResource("dailySheet.fxml"));
        sc.addScreen("DailySheet", dailySheetLoader.load(), dailySheetLoader);
        FXMLLoader addEventLoader = new FXMLLoader(getClass().getResource("addEvent.fxml"));
        sc.addScreen("AddEvent", addEventLoader.load(), addEventLoader);

        sc.addScreen("Startup", FXMLLoader.load(getClass().getResource("startup.fxml")));
        sc.addScreen("Login", FXMLLoader.load(getClass().getResource("login.fxml")));
        sc.addScreen("MainMenu", FXMLLoader.load(getClass().getResource("mainMenu.fxml")));
    }
}
