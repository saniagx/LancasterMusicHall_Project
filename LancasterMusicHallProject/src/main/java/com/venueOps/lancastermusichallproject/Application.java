package com.venueOps.lancastermusichallproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class Application extends javafx.application.Application {
    ///static Stage stage; //global stage variable
    static ScreenController sc;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws IOException {
        try {
            Scene scene = new Scene(new AnchorPane(), 1280, 720);
            sc = new ScreenController(scene);
            initializeScreens();
            ScreenController.loadScreen("Login"); // Login screen is loaded on startup

            URL imageUrl = getClass().getResource("assets/LMHLogo.png");
            Image LMHlogo = new Image(imageUrl.toExternalForm());
            stage.getIcons().add(LMHlogo);

            stage.setTitle("Lancaster's Music Hall");
            stage.setResizable(false);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeScreens() throws IOException {
        sc.addScreen("Login", FXMLLoader.load(getClass().getResource("login.fxml")));
        sc.addScreen("MainMenu", FXMLLoader.load(getClass().getResource("mainMenu.fxml")));
        sc.addScreen("UsageChart", FXMLLoader.load(getClass().getResource("usageChart.fxml")));
        sc.addScreen("Diary", FXMLLoader.load(getClass().getResource("diary.fxml")));
        sc.addScreen("DailySheet", FXMLLoader.load(getClass().getResource("dailySheet.fxml")));
    }

}
