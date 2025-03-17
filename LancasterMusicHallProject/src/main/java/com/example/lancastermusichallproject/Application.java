package com.example.lancastermusichallproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    static Stage stage; //global stage variable
    @Override
    public void start(Stage stage) throws IOException {
        Application.stage = stage; //initilaising the stage
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        stage.setTitle("Lancaster's Music Hall");

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    //calling main menu screen
    public static void mainMenuScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("mainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setScene(scene);
    }

    public static void usageChartScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("usageChart.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setScene(scene);
    }

    public static void diaryScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("diary.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setScene(scene);
    }

    public static void loginScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setScene(scene);
    }

    //need to create a daily sheet screen
    public static void dailySheetScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("mainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }

}
