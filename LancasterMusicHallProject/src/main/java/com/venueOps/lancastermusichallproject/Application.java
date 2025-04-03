package com.venueOps.lancastermusichallproject;

import javafx.animation.PauseTransition;
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

            URL imageUrl = getClass().getResource("assets/LMHLogo.png");
            Image LMHlogo = new Image(imageUrl.toExternalForm());
            stage.getIcons().add(LMHlogo);

            stage.setTitle("Lancaster's Music Hall");
            stage.setResizable(false);

            stage.setScene(scene);
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
        FXMLLoader calendarLoader = new FXMLLoader(getClass().getResource("calendar.fxml"));
        sc.addScreen("Calendar", calendarLoader.load(), calendarLoader);
        FXMLLoader dayOverviewLoader = new FXMLLoader(getClass().getResource("dayOverview.fxml"));
        sc.addScreen("DayOverview", dayOverviewLoader.load(), dayOverviewLoader);
        FXMLLoader invoiceLoader = new FXMLLoader(getClass().getResource("invoice.fxml"));
        sc.addScreen("Invoice", invoiceLoader.load(), invoiceLoader);
        sc.addScreen("Startup", FXMLLoader.load(getClass().getResource("startup.fxml")));
        sc.addScreen("Login", FXMLLoader.load(getClass().getResource("login.fxml")));
        sc.addScreen("MainMenu", FXMLLoader.load(getClass().getResource("mainMenu.fxml")));
        sc.addScreen("UsageChart", FXMLLoader.load(getClass().getResource("usageChart.fxml")));
        sc.addScreen("Diary", FXMLLoader.load(getClass().getResource("diary.fxml")));
        sc.addScreen("DailySheet", FXMLLoader.load(getClass().getResource("dailySheet.fxml")));
        sc.addScreen("BookingOverview", FXMLLoader.load(getClass().getResource("bookingOverview.fxml")));
        sc.addScreen("EventSeating", FXMLLoader.load(getClass().getResource("eventSeating.fxml")));
        sc.addScreen("AddEvent", FXMLLoader.load(getClass().getResource("addEvent.fxml")));
    }

}
