package com.venueOps.lancastermusichallproject.operations;
import com.venueOps.lancastermusichallproject.ScreenController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DayOverview {
    @FXML private Label dateLabel;

    private LocalDate date;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    @FXML
    public void initialize() {
        dateLabel.setText("Date");
    }

    public void BackButton() {
        ScreenController.loadScreen("Calendar");
    }

    public void EventOverview() {
        ScreenController.loadScreen("BookingOverview");
    }

    public void refresh() {
        date = AppData.getSelectedDate();
        dateLabel.setText(date.format(formatter));
    }
}

