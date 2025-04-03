package com.venueOps.lancastermusichallproject.operations;
import com.venueOps.lancastermusichallproject.ScreenController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DayOverview {
    @FXML private Label dateLabel;
    @FXML private VBox eventsVBox;

    private LocalDate date;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    @FXML
    public void initialize() {
        dateLabel.setText("Date");
    }

    public void refresh() {
        date = AppData.getSelectedDate();
        dateLabel.setText(date.format(formatter));
    }

    public void BackButton() {
        ScreenController.loadScreen("Calendar");
    }

    public void EventOverview() {
        ScreenController.loadScreen("BookingOverview");
    }
}

