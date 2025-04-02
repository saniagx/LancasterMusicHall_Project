package com.venueOps.lancastermusichallproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.HashMap;

public class ScreenController {

    private static HashMap<String, Pane> screenMap = new HashMap<>();
    private static HashMap<String, FXMLLoader> loaderMap = new HashMap<>(); // 🔹 Added
    private static Scene main;

    public ScreenController(Scene main) {
        ScreenController.main = main;
    }

    // New  method that accepts loader aswell
    public void addScreen(String screenName, Pane screen, FXMLLoader loader) {
        screenMap.put(screenName, screen);
        loaderMap.put(screenName, loader);
    }

    public void addScreen(String screenName, Pane screen) {
        screenMap.put(screenName, screen);
    }

    public static void loadScreen(String screenName) {
        main.setRoot(screenMap.get(screenName));
    }

    // retrieve controller from stored FXMLLoader
    public static Object getController(String screenName) {
        FXMLLoader loader = loaderMap.get(screenName);
        if (loader != null) {
            return loader.getController();
        }
        return null;
    }
}
