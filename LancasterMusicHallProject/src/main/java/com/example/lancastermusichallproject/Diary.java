
package com.example.lancastermusichallproject;

public class Diary extends Application {

    public Diary() {}

    //Back to Main Menu calls mainMenuScreen within Application
    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

}
