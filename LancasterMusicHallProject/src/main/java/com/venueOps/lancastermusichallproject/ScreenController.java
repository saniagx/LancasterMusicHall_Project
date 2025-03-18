package com.venueOps.lancastermusichallproject;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.HashMap;

public class ScreenController {
    private static HashMap<String, Pane> screenMap = new HashMap<>();
    private static Scene main;

    public ScreenController(Scene main) {
        ScreenController.main = main;
    }

    public void addScreen(String screenName, Pane screen) {
        screenMap.put(screenName, screen);
    }

    public static void loadScreen(String screenName) {
        main.setRoot(screenMap.get(screenName));
    }
}
