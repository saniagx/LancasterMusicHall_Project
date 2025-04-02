package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

public class AddEvent {

    @FXML private TextField eventNameField;
    @FXML private TextField eventTypeField;
    @FXML private TextField hostField;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;

    @FXML private TextField priceField;
    @FXML private TextField venueIDField;
    @FXML private TextField venueNameField;

    public void handleSubmit() {
        try {
            String name = eventNameField.getText();
            String type = eventTypeField.getText();
            String host = hostField.getText();

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            LocalTime startTime = LocalTime.parse(startTimeField.getText());
            LocalTime endTime = LocalTime.parse(endTimeField.getText());

            BigDecimal price = new BigDecimal(priceField.getText());
            int venueID = Integer.parseInt(venueIDField.getText());
            String venueName = venueNameField.getText();

            Event newEvent = new Event(
                    generateEventID(),
                    name,
                    type,
                    host,
                    LocalDateTime.of(startDate, startTime),
                    LocalDateTime.of(endDate, endTime),
                    price,
                    venueID,
                    venueName,
                    new HashMap<>() // Pass empty usage map
            );

            // Add to calendar instance
            Calendar calendarController = (Calendar) ScreenController.getController("Calendar");
            if (calendarController != null) {
                calendarController.addEvent(newEvent);
                calendarController.refreshCalendar();
            }

            ScreenController.loadScreen("Calendar");

        } catch (Exception e) {
            showError("Please fill all fields correctly.");
            e.printStackTrace();
        }
    }

    public void handleCancel() {
        ScreenController.loadScreen("Calendar");
    }

    private int generateEventID() {
        return (int) (System.currentTimeMillis() % 100000);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
