package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEvent {

    @FXML private TextField eventNameField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField startTime_HourField;
    @FXML private TextField startTime_MinuteField;
    @FXML private TextField endTime_HourField;
    @FXML private TextField endTime_MinuteField;
    @FXML private Text ticketPriceText;
    @FXML private HBox ticketPriceHBox;
    @FXML private TextField ticketPriceField;
    @FXML private Text maxDiscountText;
    @FXML private HBox maxDiscountHBox;
    @FXML private TextField maxDiscountField;
    @FXML private ComboBox<String> venueComboBox;

    @FXML private Tab mainHall_Tab;
    @FXML private Tab smallHall_Tab;
    @FXML private Tab meetingRooms_Tab;

    @FXML private ComboBox<String> greenRoom_ComboBox;
    @FXML private ComboBox<String> bronteBoardroom_ComboBox;
    @FXML private ComboBox<String> dickensDen_ComboBox;
    @FXML private ComboBox<String> poeParlor_ComboBox;
    @FXML private ComboBox<String> globeRoom_ComboBox;
    @FXML private ComboBox<String> chekhovChamber_ComboBox;

    private final Map<String, Integer> venueNametoID = Map.of(
            "Main Hall", 0,
            "Small Hall", 1,
            "Rehearsal Space", 2,
            "The Green Room", 3,
            "Bronte Boardroom", 4,
            "Dickens Den", 5,
            "Poe Parlor", 6,
            "Globe Room", 7,
            "Chekhov Chamber", 8
    );
    private List<String> venues = List.of("Main Hall", "Small Hall", "Rehearsal Space", "The Green Room", "Bronte Boardroom",
            "Dickens Den", "Poe Parlor", "Globe Room", "Chekhov Chamber");
    private List<String> halls = venues.subList(0,3);
    private List<String> meetingRooms = venues.subList(3, venues.size());

    @FXML
    public void initialize() {
        disableByDefault();

        // Populate with venue options
        venueComboBox.getItems().addAll(venues);

        List<ComboBox<String>> meetingRoomComboBoxes = List.of(greenRoom_ComboBox, bronteBoardroom_ComboBox, dickensDen_ComboBox,
                poeParlor_ComboBox, globeRoom_ComboBox, chekhovChamber_ComboBox);
        for (ComboBox<String> comboBox : meetingRoomComboBoxes) {
            comboBox.getItems().addAll(
                    "Classroom",
                    "Boardroom",
                    "Presentation"
            );
        }

        // Venue combo box listener
        venueComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Enable/disable Main Hall tab
                mainHall_Tab.setDisable(!newValue.equals("Main Hall"));

                // Enable/disable Small Hall tab
                smallHall_Tab.setDisable(!newValue.equals("Small Hall"));

                // Enable/disable Meeting Rooms tab
                boolean isMeetingRoom = meetingRooms.contains(newValue);
                meetingRooms_Tab.setDisable(!isMeetingRoom);

                // Enable/disable Meeting Rooms combo boxes
                greenRoom_ComboBox.setDisable(!newValue.equals("The Green Room"));
                bronteBoardroom_ComboBox.setDisable(!newValue.equals("Bronte Boardroom"));
                dickensDen_ComboBox.setDisable(!newValue.equals("Dickens Den"));
                poeParlor_ComboBox.setDisable(!newValue.equals("Poe Parlor"));
                globeRoom_ComboBox.setDisable(!newValue.equals("Globe Room"));
                chekhovChamber_ComboBox.setDisable(!newValue.equals("Chekhov Chamber"));

                // Show/hide ticket price and discount fields for Main Hall or Small Hall only
                boolean isHall = newValue.equals("Main Hall") || newValue.equals("Small Hall");
                ticketPriceText.setVisible(isHall);
                ticketPriceHBox.setVisible(isHall);
                maxDiscountText.setVisible(isHall);
                maxDiscountHBox.setVisible(isHall);
            }
        });
    }

    public void Submit() {
        try {
            String name = eventNameField.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            LocalTime startTime = LocalTime.of(Integer.parseInt(startTime_HourField.getText()), Integer.parseInt(startTime_MinuteField.getText()));
            LocalTime endTime = LocalTime.of(Integer.parseInt(endTime_HourField.getText()), Integer.parseInt(endTime_MinuteField.getText()));
            String venueName = venueComboBox.getSelectionModel().getSelectedItem();
            int venueID = venueNametoID.get(venueName);

            BigDecimal ticketPrice;
            double maxDiscount;
            if (venueName.equals("Main Hall") || venueName.equals("Small Hall")) {
                ticketPrice = new BigDecimal(ticketPriceField.getText());
                maxDiscount = Double.parseDouble(maxDiscountField.getText());
            } else {
                ticketPrice = BigDecimal.ZERO;
                maxDiscount = 0.0;
            }

            Event newEvent = new Event(
                    generateBookingID(), // This needs to be the same ID for all events under this booking
                    generateEventID(),
                    name,
                    getEventType(venueName),
                    "temp",
                    LocalDateTime.of(startDate, startTime),
                    LocalDateTime.of(endDate, endTime),
                    calculateCost(),
                    ticketPrice,
                    maxDiscount,
                    venueID,
                    venueName,
                    new HashMap<>() // Pass empty usage map
            );

            // Add to calendar instance
            BookingOverview bookingOverviewController = (BookingOverview) ScreenController.getController("BookingOverview");
            if (bookingOverviewController != null) {
                bookingOverviewController.addEventToList(newEvent);
                bookingOverviewController.refresh();
            }

            ScreenController.loadScreen("BookingOverview");

        } catch (Exception e) {
            showError("Please fill all fields correctly.");
            e.printStackTrace();
        }
    }

    private int generateBookingID() {
        return (int) (System.currentTimeMillis() % 100000);
    }

    private int generateEventID() {
        return (int) (System.currentTimeMillis() % 100000);
    }

    private String getEventType(String selectedVenue) {
        if (halls.contains(selectedVenue)) {
            return "Event";
        } else if (meetingRooms.contains(selectedVenue)) {
            return "Meeting";
        } else {
            System.err.println("Invalid event type");
            return "N/A";
        }
    }

    // To be completed, Use Lancaster's Music Hall's rate card
    private BigDecimal calculateCost() {
        return BigDecimal.ZERO;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void disableByDefault() {
        // Disable layout tabs by default
        mainHall_Tab.setDisable(true);
        smallHall_Tab.setDisable(true);
        meetingRooms_Tab.setDisable(true);
        // Disable all ComboBoxes by default
        greenRoom_ComboBox.setDisable(true);
        bronteBoardroom_ComboBox.setDisable(true);
        dickensDen_ComboBox.setDisable(true);
        poeParlor_ComboBox.setDisable(true);
        globeRoom_ComboBox.setDisable(true);
        chekhovChamber_ComboBox.setDisable(true);
        // Hide ticket price and max discount rows
        ticketPriceHBox.setVisible(false);
        ticketPriceText.setVisible(false);
        maxDiscountHBox.setVisible(false);
        maxDiscountText.setVisible(false);
    }

    public void BackButton() { ScreenController.loadScreen("BookingOverview"); }
}