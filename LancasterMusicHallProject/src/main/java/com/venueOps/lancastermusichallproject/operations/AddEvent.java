package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEvent {
    // Event Details tab attributes
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
    @FXML private ComboBox<String> layoutComboBox;
    @FXML private ComboBox<Integer> tablesComboBox;

    @FXML private Tab SeatingConfig_Tab;

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
    private List<String> mainHall_Layouts = List.of("Default", "No Balconies", "Empty", "Dinner");
    private List<String> smallHall_Layouts = List.of("Default", "Dinner");
    private List<String> meeting_Layouts = List.of("Classroom", "Boardroom", "Presentation");

    @FXML
    public void initialize() {
        // Disable certain fields by default
        disableByDefault();

        rows = List.of(RowAA_CheckComboBox, RowBB_CheckComboBox, RowCC_CheckComboBox, RowA_CheckComboBox,
                RowB_CheckComboBox, RowC_CheckComboBox, RowD_CheckComboBox, RowE_CheckComboBox, RowF_CheckComboBox, RowG_CheckComboBox,
                RowH_CheckComboBox, RowI_CheckComboBox, RowJ_CheckComboBox, RowK_CheckComboBox, RowL_CheckComboBox, RowM_CheckComboBox,
                RowN_CheckComboBox, RowO_CheckComboBox, RowP_CheckComboBox);

        // Populate with venue options
        venueComboBox.getItems().setAll(venues);
        // Populate table combo box
        tablesComboBox.getItems().setAll(List.of(1, 2, 3, 4, 5, 6));

        // Initialize ObservableLists
        unavailableSeats = FXCollections.observableArrayList();
        restrictedViews = FXCollections.observableArrayList();
        // Set ListView items
        UnavailableSeats_ListView.setItems(unavailableSeats);
        RestrictedViews_ListView.setItems(restrictedViews);
        // Populate list selector combo box
        chooseListComboBox.getItems().setAll(List.of("Unavailable Seats", "Restricted Views"));
        chooseListComboBox.getSelectionModel().selectFirst();

        // Venue combo box listener
        venueComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Enable/disable Hall Layout tab
                SeatingConfig_Tab.setDisable(!(newValue.equals("Main Hall") || newValue.equals("Small Hall")));

                // Populate row combo boxes on SeatingConfig tab
                populateRows();

                // Enable/disable Meeting Room Combo Box
                boolean isMeetingRoom = meetingRooms.contains(newValue);

                // Show/hide ticket price and discount fields for Main Hall or Small Hall only
                boolean isHall = newValue.equals("Main Hall") || newValue.equals("Small Hall");
                ticketPriceText.setVisible(isHall);
                ticketPriceHBox.setVisible(isHall);
                maxDiscountText.setVisible(isHall);
                maxDiscountHBox.setVisible(isHall);

                // Layout handler
                layoutComboBox.setVisible(isMeetingRoom || isHall);
                tablesComboBox.setVisible(false);
                if (isMeetingRoom) {
                    layoutComboBox.getItems().setAll(meeting_Layouts);
                } else if (newValue.equals("Main Hall")) {
                    layoutComboBox.getItems().setAll(mainHall_Layouts);
                    layoutComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldLayoutValue, newLayoutValue) -> {
                        handleMainHallLayout(newLayoutValue);
                    });
                } else if (newValue.equals("Small Hall")) {
                    layoutComboBox.getItems().setAll(smallHall_Layouts);
                    layoutComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldLayoutValue, newLayoutValue) -> {
                        handleSmallHallLayout(newLayoutValue);
                    });
                } else {
                    layoutComboBox.setVisible(false);
                }
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
        SeatingConfig_Tab.setDisable(true);
        // Hide layout combo box by default
        layoutComboBox.setVisible(false);
        // Hide tables combo box by default
        tablesComboBox.setVisible(false);
        // Hide ticket price and max discount rows
        ticketPriceHBox.setVisible(false);
        ticketPriceText.setVisible(false);
        maxDiscountHBox.setVisible(false);
        maxDiscountText.setVisible(false);
    }

    public void BackButton() { ScreenController.loadScreen("BookingOverview"); }

    // SEATING CONFIG TAB

    // SeatingConfig tab attributes
    // Row checkboxes
    @FXML private CheckComboBox<Integer> RowAA_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowBB_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowCC_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowA_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowB_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowC_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowD_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowE_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowF_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowG_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowH_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowI_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowJ_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowK_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowL_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowM_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowN_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowO_CheckComboBox;
    @FXML private CheckComboBox<Integer> RowP_CheckComboBox;
    private List<CheckComboBox> rows;

    // List management
    @FXML private ComboBox<String> chooseListComboBox;
    @FXML private Button updateListButton;
    @FXML private ListView<String> UnavailableSeats_ListView;
    @FXML private Button clearUnavailableSeats_Button;
    @FXML private ListView<String> RestrictedViews_ListView;
    @FXML private Button clearRestrictedViews_Button;
    private ObservableList<String> unavailableSeats;
    private ObservableList<String> restrictedViews;

    // Images
    @FXML private ImageView seatMap_Image;
    Image mainHall_default = new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/MainHall_Default.png").toExternalForm());
    Image mainHall_noBalconies = new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/MainHall_NoBalconies.png").toExternalForm());
    Image mainHall_empty = new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/MainHall_Flexible.png").toExternalForm());
    Image mainHall_dinner1 = new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/MainHall_Dinner1.png").toExternalForm());
    Image mainHall_dinner2 = new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/MainHall_Dinner2.png").toExternalForm());
    Image mainHall_dinner3 = new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/MainHall_Dinner3.png").toExternalForm());
    Image mainHall_dinner4 = new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/MainHall_Dinner4.png").toExternalForm());
    Image mainHall_dinner5 = new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/MainHall_Dinner5.png").toExternalForm());
    Image mainHall_dinner6 = new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/MainHall_Dinner6.png").toExternalForm());
    Image smallHall_default = new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/SmallHall_Default.png").toExternalForm());
    Image smallHall_dinner = new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/SmallHall_Dinner.png").toExternalForm());

    public void populateRows() {
        int seatCount;
        if (venueComboBox.getSelectionModel().getSelectedItem().equals("Main Hall")) {
            for (CheckComboBox checkComboBox : rows) {
                String id = checkComboBox.getId();
                switch (id) {
                    // Balcony rows
                    case "RowAA_CheckComboBox":
                        seatCount = 53;
                        break;
                    case "RowBB_CheckComboBox":
                        seatCount = 28;
                        break;
                    case "RowCC_CheckComboBox":
                        seatCount = 8;
                        break;

                    // Stall rows
                    case "RowL_CheckComboBox":
                        seatCount = 16;
                        break;
                    case "RowN_CheckComboBox":
                        seatCount = 20;
                        break;
                    case "RowO_CheckComboBox":
                        seatCount = 11;
                        break;
                    case "RowP_CheckComboBox":
                        seatCount = 10;
                        break;
                    case "RowA_CheckComboBox", "RowB_CheckComboBox", "RowC_CheckComboBox", "RowD_CheckComboBox",
                         "RowE_CheckComboBox", "RowF_CheckComboBox",
                         "RowG_CheckComboBox", "RowH_CheckComboBox", "RowI_CheckComboBox", "RowJ_CheckComboBox",
                         "RowK_CheckComboBox", "RowM_CheckComboBox":
                        seatCount = 19;
                        break;
                    default:
                        seatCount = 0;
                        System.err.println("Invalid row id: " + id);
                        break;
                }
                // Generate seat numbers and add to the CheckComboBox
                ObservableList<String> seats = FXCollections.observableArrayList();
                String rowLetter = id.replace("Row", "").replace("_CheckComboBox", "");
                for (int i = 1; i <= seatCount; i++) {
                    seats.add(rowLetter + i);
                }
                checkComboBox.getItems().setAll(seats);
            }
        } else if (venueComboBox.getSelectionModel().getSelectedItem().equals("Small Hall")) {
            for (CheckComboBox checkComboBox : rows) {
                String id = checkComboBox.getId();
                switch (id) {
                    case "RowA_CheckComboBox", "RowB_CheckComboBox", "RowC_CheckComboBox":
                        seatCount = 8;
                        break;
                    case "RowD_CheckComboBox", "RowE_CheckComboBox", "RowF_CheckComboBox", "RowG_CheckComboBox",
                         "RowH_CheckComboBox", "RowI_CheckComboBox", "RowJ_CheckComboBox", "RowK_CheckComboBox", "RowL_CheckComboBox":
                        seatCount = 7;
                        break;
                    case "RowM_CheckComboBox", "RowN_CheckComboBox":
                        seatCount = 4;
                        break;
                    default:
                        seatCount = 0;
                        System.err.println("Invalid row id: " + id);
                        break;
                }
                // Generate seat numbers and add to the CheckComboBox
                ObservableList<String> seats = FXCollections.observableArrayList();
                String rowLetter = id.replace("Row", "").replace("_CheckComboBox", "");
                for (int i = 1; i <= seatCount; i++) {
                    seats.add(rowLetter + i);
                }
                checkComboBox.getItems().setAll(seats);
            }
        }
    }

    private void handleMainHallLayout(String layout) {
        if (layout == null) {
            return;
        }
        tablesComboBox.setVisible(false);
        // Enable all rows
        for (CheckComboBox checkComboBox : rows) {
            checkComboBox.setDisable(false);
        }
        enableListManagement();
        ClearRestrictedViews();
        ClearUnavailableSeats();
        switch (layout) {
            // Add logic to set venue seating layout
            case "Default":
                seatMap_Image.setImage(mainHall_default);

                break;
            case "No Balconies":
                seatMap_Image.setImage(mainHall_noBalconies);
                // Disable all balconies
                RowAA_CheckComboBox.setDisable(true);
                RowBB_CheckComboBox.setDisable(true);
                RowCC_CheckComboBox.setDisable(true);
                break;
            case "Empty":
                // Disable all rows
                seatMap_Image.setImage(mainHall_empty);
                for (CheckComboBox checkComboBox : rows) {
                    checkComboBox.setDisable(true);
                }
                disableListManagement();
                break;
            case "Dinner":
                seatMap_Image.setImage(mainHall_dinner1);
                tablesComboBox.setVisible(true);
                for (CheckComboBox checkComboBox : rows) {
                    checkComboBox.setDisable(true);
                }
                disableListManagement();
                tablesComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldLayoutValue, newLayoutValue) -> {
                    handleTablesLayout(newLayoutValue);
                });
                break;
            default:
                seatMap_Image.setImage(mainHall_default);
                break;
        }
    }

    private void handleSmallHallLayout(String layout) {
        if (layout == null) {
            return;
        }
        tablesComboBox.setVisible(false);
        // Disable all rows
        for (CheckComboBox checkComboBox : rows) {
            checkComboBox.setDisable(true);
        }
        enableListManagement();
        ClearRestrictedViews();
        ClearUnavailableSeats();
        switch (layout) {
            case "Default":
                seatMap_Image.setImage(smallHall_default);
                List<CheckComboBox> smallHallDefaultRows = rows.subList(3, 17);
                for (CheckComboBox checkComboBox : smallHallDefaultRows) {
                    checkComboBox.setDisable(false);
                }
                break;
            case "Dinner":
                seatMap_Image.setImage(smallHall_dinner);
                disableListManagement();
                break;
            default:
                seatMap_Image.setImage(smallHall_default);
                break;
        }
    }

    private void handleTablesLayout(Integer layout) {
        if (layout == null) {
            return;
        }
        switch (layout) {
            case 1:
                seatMap_Image.setImage(mainHall_dinner1);
                break;
            case 2:
                seatMap_Image.setImage(mainHall_dinner2);
                break;
            case 3:
                seatMap_Image.setImage(mainHall_dinner3);
                break;
            case 4:
                seatMap_Image.setImage(mainHall_dinner4);
                break;
            case 5:
                seatMap_Image.setImage(mainHall_dinner5);
                break;
            case 6:
                seatMap_Image.setImage(mainHall_dinner6);
                break;
            default:
                seatMap_Image.setImage(mainHall_dinner1);
        }
    }

    private void enableListManagement() {
        chooseListComboBox.setDisable(false);
        updateListButton.setDisable(false);
        UnavailableSeats_ListView.setDisable(false);
        clearUnavailableSeats_Button.setDisable(false);
        RestrictedViews_ListView.setDisable(false);
        clearRestrictedViews_Button.setDisable(false);
    }

    private void disableListManagement() {
        chooseListComboBox.setDisable(true);
        updateListButton.setDisable(true);
        UnavailableSeats_ListView.setDisable(true);
        clearUnavailableSeats_Button.setDisable(true);
        RestrictedViews_ListView.setDisable(true);
        clearRestrictedViews_Button.setDisable(true);
    }

    public void UpdateList() {
        // Get selected seats from all CheckComboBoxes
        ObservableList<String> selectedSeats = FXCollections.observableArrayList();
        for (CheckComboBox<String> checkComboBox : rows) {
            selectedSeats.addAll(checkComboBox.getCheckModel().getCheckedItems());
        }

        // Determine which list to update
        String selectedList = chooseListComboBox.getSelectionModel().getSelectedItem();
        if (selectedList != null) {
            switch (selectedList) {
                case "Unavailable Seats":
                    unavailableSeats.clear();
                    unavailableSeats.addAll(selectedSeats);
                    break;
                case "Restricted Views":
                    restrictedViews.clear();
                    restrictedViews.addAll(selectedSeats);
                    break;
            }
        }
    }

    public void ClearUnavailableSeats() {
        unavailableSeats.clear();
        // Uncheck all seats in CheckComboBoxes
        for (CheckComboBox<String> checkComboBox : rows) {
            checkComboBox.getCheckModel().clearChecks();
        }
    }

    public void ClearRestrictedViews() {
        restrictedViews.clear();
        // Uncheck all seats in CheckComboBoxes
        for (CheckComboBox<String> checkComboBox : rows) {
            checkComboBox.getCheckModel().clearChecks();
        }
    }
}