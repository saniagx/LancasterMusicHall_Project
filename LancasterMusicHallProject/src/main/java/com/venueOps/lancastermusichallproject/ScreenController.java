package com.venueOps.lancastermusichallproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.HashMap;

/**
 * Class that manages and handles the loading of all screens
 * @author Neil Daya
 * @author Meer Ali
 * @version 2.0 April 6 2025
 */
public class ScreenController {

    private static Scene main;
    private static HashMap<String, FXMLLoader> loaderMap = new HashMap<>();
    private static HashMap<String, Pane> screenMap = new HashMap<>();

    public ScreenController(Scene main) {
        ScreenController.main = main;
    }

    /**
     * Initialises a screen with a loader
     * @param screenName name of the screen to be added
     * @param screen screen object
     * @param loader loader object
     * @see javafx.stage.Screen
     * @see FXMLLoader
     */
    public void addScreen(String screenName, Pane screen, FXMLLoader loader) {
        screenMap.put(screenName, screen);
        loaderMap.put(screenName, loader);
    }

    /**
     * Initialises a screen without a loader
     * @param screenName name of the screen to be added
     * @param screen screen object
     * @see javafx.stage.Screen
     */
    public void addScreen(String screenName, Pane screen) {
        screenMap.put(screenName, screen);
    }

    public static void loadScreen(String screenName) {
        main.setRoot(screenMap.get(screenName));
    }

    /**
     * Gets the controller object for the given screen
     * @param screenName name of the screen to get the controller for
     * @return Controller for the given screen
     * @see ScreenController
     */
    public static Object getController(String screenName) {
        FXMLLoader loader = loaderMap.get(screenName);
        if (loader != null) {
            return loader.getController();
        }
        return null;
    }
}
