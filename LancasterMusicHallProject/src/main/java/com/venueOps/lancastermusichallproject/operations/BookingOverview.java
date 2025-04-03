package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import org.controlsfx.control.CheckComboBox;

public class BookingOverview {
    @FXML private CheckComboBox<String> venueComboBox;

    @FXML
    public void initialize() {
        // Populate with venue options
        venueComboBox.getItems().addAll(
                "Main Hall",
                "Small Hall",
                "Rehearsal Space",
                "The Green Room",
                "Bronte Boardroom",
                "Dickens Den",
                "Poe Parlor",
                "Globe Room",
                "Chekhov Chamber"
        );

        venueComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
            System.out.println("Selected venues: " + venueComboBox.getCheckModel().getCheckedItems());
        });
    }

    public void BackButton() {
        ScreenController.loadScreen("DayOverview");
    }

    public void EventSeating() { ScreenController.loadScreen("EventSeating"); }

    public void ConfirmBooking() { ScreenController.loadScreen("Invoice"); }
}
