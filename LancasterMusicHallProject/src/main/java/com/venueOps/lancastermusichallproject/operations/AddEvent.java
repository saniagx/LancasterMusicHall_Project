package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
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

    @FXML private TabPane tabPane;
    @FXML private Tab SeatingConfig_Tab;
    @FXML private Tab EventDetails_Tab;

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
    private List<String> VENUES = AppData.getVenues();
    private List<String> halls = VENUES.subList(0,3);
    private List<String> meetingRooms = VENUES.subList(3, VENUES.size());
    private List<String> mainHall_Layouts = List.of("Default", "No Balconies", "Empty", "Dinner");
    private List<String> smallHall_Layouts = List.of("Default", "Dinner");
    private List<String> meeting_Layouts = List.of("Classroom", "Boardroom", "Presentation");

    @FXML
    public void initialize() {
        ClearFields();

        rows = List.of(RowAA_CheckComboBox, RowBB_CheckComboBox, RowCC_CheckComboBox, RowA_CheckComboBox,
                RowB_CheckComboBox, RowC_CheckComboBox, RowD_CheckComboBox, RowE_CheckComboBox, RowF_CheckComboBox, RowG_CheckComboBox,
                RowH_CheckComboBox, RowI_CheckComboBox, RowJ_CheckComboBox, RowK_CheckComboBox, RowL_CheckComboBox, RowM_CheckComboBox,
                RowN_CheckComboBox, RowO_CheckComboBox, RowP_CheckComboBox);

        // Populate with venue options
        refreshAvailableVenues();
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

        // Listen to date pickers to change which venues are available
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshAvailableVenues();
                venueComboBox.getSelectionModel().clearSelection();
            }
        });
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshAvailableVenues();
                venueComboBox.getSelectionModel().clearSelection();
            }
        });

        layoutComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldLayoutValue, newLayoutValue) -> {
            String selectedVenue = venueComboBox.getSelectionModel().getSelectedItem();
            if (selectedVenue != null && newLayoutValue != null) {
                updateCapacity();

                if (selectedVenue.equals("Main Hall")) {
                    handleMainHallLayout(newLayoutValue);
                } else if (selectedVenue.equals("Small Hall")) {
                    handleSmallHallLayout(newLayoutValue);
                }
            }
        });

        // Venue combo box listener
        venueComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Enable/disable Hall Layout tab
                SeatingConfig_Tab.setDisable(!(newValue.equals("Main Hall") || newValue.equals("Small Hall")));

                // Populate row combo boxes on SeatingConfig tab
                populateRows();
                updateCapacity();

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
                    layoutComboBox.getSelectionModel().selectFirst();
                } else if (newValue.equals("Small Hall")) {
                    layoutComboBox.getItems().setAll(smallHall_Layouts);
                    layoutComboBox.getSelectionModel().selectFirst();
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
            LocalDateTime start = LocalDateTime.of(startDate, startTime);
            LocalDateTime end = LocalDateTime.of(endDate, endTime);
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
                    0, // Will be replaced with a proper value when booking is confirmed
                    0, // Will be replaced with a proper value when booking is confirmed
                    name,
                    getEventType(venueName),
                    "temp",
                    start,
                    end,
                    calculateCost(venueName, start, end),
                    ticketPrice,
                    maxDiscount,
                    venueID,
                    venueName,
                    new HashMap<>(), // Pass empty usage map
                    new SeatingConfig(0, capacity, getLayout(), venueName, restrictedViews)
            );

            System.out.println("New Seating Config:\nCapacity: " + capacity + "\nLayout: " + getLayout() + "\nVenue: " + venueName + "\nTicket Price: ");

            // Add to booking instance
            NewBooking newBookingController = (NewBooking) ScreenController.getController("NewBooking");
            if (newBookingController != null) {
                newBookingController.addEventToList(newEvent);
                newBookingController.refresh();
            }

            AppData.addEventToCurrentBookingEvents(newEvent);

            ScreenController.loadScreen("NewBooking");
            ClearFields();
        } catch (Exception e) {
            showError("Please fill all fields correctly.");
            System.err.println("Failed to add event: " + e.getMessage());
        } finally {
            tabPane.getSelectionModel().select(EventDetails_Tab);
        }
    }

    private String getLayout() throws Exception {
        if (venueComboBox.getSelectionModel().getSelectedItem().equals("Main Hall") && layoutComboBox.getSelectionModel().getSelectedItem().equals("Dinner")) {
            return "Dinner with " + tablesComboBox.getSelectionModel().getSelectedItem() + " tables";
        } else if (venueComboBox.getSelectionModel().getSelectedItem().equals("Rehearsal Space")) {
            return "Rehearsal Space";
        } else {
            if (layoutComboBox.getSelectionModel().getSelectedItem() == null) {
                throw new Exception("Layout cannot be null");
            }
            return layoutComboBox.getSelectionModel().getSelectedItem();
        }
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

    private BigDecimal calculateCost(String venueName, LocalDateTime startDate, LocalDateTime endDate) {
        double VAT = 0.2;
        Duration duration = Duration.between(startDate, endDate);
        long hours = duration.toHours();
        long days = duration.toDays();
        double hourlyRate = 0;
        double dailyRate = 0;

        switch (venueName) {
            case "Main Hall":
                hourlyRate = 325;
                dailyRate = 3800;
                break;
            case "Small Hall":
                hourlyRate = 225;
                dailyRate = 2200;
                break;
            case "Rehearsal Space":
                hourlyRate = 60;
                dailyRate = 450;
                break;
            case "The Green Room":
                hourlyRate = 25;
                dailyRate = 130;
                break;
            case "Bronte Boardroom":
                hourlyRate = 40;
                dailyRate = 200;
                break;
            case "Dickens Den":
                hourlyRate = 30;
                dailyRate = 150;
                break;
            case "Poe Parlor":
                hourlyRate = 35;
                dailyRate = 170;
                break;
            case "Globe Room":
                hourlyRate = 50;
                dailyRate = 250;
                break;
            case "Chekhov Chamber":
                hourlyRate = 38;
                dailyRate = 180;
                break;
            default:
                System.out.println("Invalid venue for cost calculation");
        }

        double baseCost;
        if (days > 1) {
            // Use daily rate for multi-day events
            baseCost = days * dailyRate;
        } else {
            // Use hourly rate for events ≤ 1 day
            baseCost = hours * hourlyRate;
        }

        // Apply VAT and return total
        return BigDecimal.valueOf(baseCost * (1 + VAT));

    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void BackButton() {
        ScreenController.loadScreen("NewBooking");
        tabPane.getSelectionModel().select(EventDetails_Tab);
    }

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

    // Capacity
    private final int MAIN_HALL_MAX_CAPACITY = 374;
    private final int SMALL_HALL_MAX_CAPACITY = 95;
    private int capacity;
    @FXML private Label capacityLabel;

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
                seatMap_Image.setFitWidth(800);
                seatMap_Image.setFitHeight(897);
                break;
            case "No Balconies":
                seatMap_Image.setImage(mainHall_noBalconies);
                seatMap_Image.setFitWidth(600);
                seatMap_Image.setFitHeight(760);
                // Disable all balconies
                RowAA_CheckComboBox.setDisable(true);
                RowBB_CheckComboBox.setDisable(true);
                RowCC_CheckComboBox.setDisable(true);
                break;
            case "Empty":
                seatMap_Image.setImage(mainHall_empty);
                seatMap_Image.setFitWidth(600);
                seatMap_Image.setFitHeight(760);
                // Disable all rows
                for (CheckComboBox checkComboBox : rows) {
                    checkComboBox.setDisable(true);
                }
                disableListManagement();
                break;
            case "Dinner":
                seatMap_Image.setImage(mainHall_dinner1);
                seatMap_Image.setFitWidth(600);
                seatMap_Image.setFitHeight(760);
                tablesComboBox.setVisible(true);
                // Disable all rows
                for (CheckComboBox checkComboBox : rows) {
                    checkComboBox.setDisable(true);
                }
                disableListManagement();
                tablesComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldLayoutValue, newLayoutValue) -> {
                    updateCapacity();
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
        seatMap_Image.setFitWidth(514);
        seatMap_Image.setFitHeight(771);
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
                    unavailableSeats.setAll(selectedSeats);
                    break;
                case "Restricted Views":
                    restrictedViews.setAll(selectedSeats);
                    break;
            }
        }
        for (CheckComboBox<String> checkComboBox : rows) {
            checkComboBox.getCheckModel().clearChecks();
        }
        updateCapacity();
    }

    public void ClearUnavailableSeats() {
        unavailableSeats.clear();
        // Uncheck all seats in CheckComboBoxes
        for (CheckComboBox<String> checkComboBox : rows) {
            checkComboBox.getCheckModel().clearChecks();
        }
        updateCapacity();
    }

    public void ClearRestrictedViews() {
        restrictedViews.clear();
        // Uncheck all seats in CheckComboBoxes
        for (CheckComboBox<String> checkComboBox : rows) {
            checkComboBox.getCheckModel().clearChecks();
        }
    }

    private void updateCapacity() {
        if (venueComboBox.getSelectionModel().getSelectedItem() == null || layoutComboBox.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        int unavailableCount = unavailableSeats.size();
        switch (venueComboBox.getSelectionModel().getSelectedItem()) {
            case "Main Hall":
                switch (layoutComboBox.getSelectionModel().getSelectedItem()) {
                    case "Default":
                        capacity = MAIN_HALL_MAX_CAPACITY - unavailableCount;
                        break;
                    case "No Balconies":
                        capacity = MAIN_HALL_MAX_CAPACITY - 89 - unavailableCount;
                        break;
                    case "Dinner":
                        capacity = 8 * (tablesComboBox.getSelectionModel().getSelectedItem() == null ? 0 : tablesComboBox.getSelectionModel().getSelectedItem());
                        break;
                    default:
                        capacity = MAIN_HALL_MAX_CAPACITY;
                        break;
                }
                break;
            case "Small Hall":
                switch (layoutComboBox.getSelectionModel().getSelectedItem()) {
                    case "Default":
                        capacity = SMALL_HALL_MAX_CAPACITY - unavailableCount;
                        break;
                    case "Dinner":
                        capacity = 18;
                        break;
                    default:
                        capacity = SMALL_HALL_MAX_CAPACITY;
                        break;
                }
                break;
            case "The Green Room":
                switch (layoutComboBox.getSelectionModel().getSelectedItem()) {
                    case "Classroom":
                        capacity = 12;
                        break;
                    case "Boardroom":
                        capacity = 10;
                        break;
                    case "Presentation":
                        capacity = 20;
                        break;
                    default:
                        capacity = 0;
                        break;
                }
                break;
            case "Bronte Boardroom":
                switch (layoutComboBox.getSelectionModel().getSelectedItem()) {
                    case "Classroom":
                        capacity = 25;
                        break;
                    case "Boardroom":
                        capacity = 18;
                        break;
                    case "Presentation":
                        capacity = 40;
                        break;
                    default:
                        capacity = 0;
                        break;
                }
                break;
            case "Dickens Den":
                switch (layoutComboBox.getSelectionModel().getSelectedItem()) {
                    case "Classroom":
                        capacity = 15;
                        break;
                    case "Boardroom":
                        capacity = 12;
                        break;
                    case "Presentation":
                        capacity = 25;
                        break;
                    default:
                        capacity = 0;
                        break;
                }
                break;
            case "Poe Parlor":
                switch (layoutComboBox.getSelectionModel().getSelectedItem()) {
                    case "Classroom":
                        capacity = 20;
                        break;
                    case "Boardroom":
                        capacity = 14;
                        break;
                    case "Presentation":
                        capacity = 30;
                        break;
                    default:
                        capacity = 0;
                        break;
                }
                break;
            case "Globe Room":
                switch (layoutComboBox.getSelectionModel().getSelectedItem()) {
                    case "Classroom":
                        capacity = 30;
                        break;
                    case "Boardroom":
                        capacity = 20;
                        break;
                    case "Presentation":
                        capacity = 50;
                        break;
                    default:
                        capacity = 0;
                        break;
                }
                break;
            case "Chekhov Chamber":
                switch (layoutComboBox.getSelectionModel().getSelectedItem()) {
                    case "Classroom":
                        capacity = 18;
                        break;
                    case "Boardroom":
                        capacity = 16;
                        break;
                    case "Presentation":
                        capacity = 35;
                        break;
                    default:
                        capacity = 0;
                        break;
                }
                break;
            default:
                capacity = 0;
                break;
        }
        capacityLabel.setText("Capacity: " + capacity);
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

    public void ClearFields() {
        eventNameField.setText("");
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        startTime_HourField.setText("");
        startTime_MinuteField.setText("");
        endTime_HourField.setText("");
        endTime_MinuteField.setText("");
        disableByDefault();
        venueComboBox.getSelectionModel().clearSelection();
        layoutComboBox.getSelectionModel().clearSelection();
        tablesComboBox.getSelectionModel().clearSelection();
        ticketPriceField.setText("");
        maxDiscountField.setText("");
    }

    public List<String> getAvailableVenues() {
        List<String> availableVenues = new ArrayList<>();
        Calendar calendarController = (Calendar) ScreenController.getController("Calendar");
        LocalDateTime start = startDatePicker.getValue().atStartOfDay();
        LocalDateTime end = endDatePicker.getValue().atTime(23, 59, 59);
        for (String venue : VENUES) {
            if (calendarController != null) {
                if (calendarController.isVenueAvailable(start, end, venue)
                        && AppData.getCurrentBookingEvents().stream()
                        .filter(e -> e.getVenueName().equals(venue))
                        .allMatch(e -> !(start.isBefore(e.getEventEnd()) && end.isAfter(e.getEventStart()))))
                {
                    availableVenues.add(venue);
                }
            }
        }
        return availableVenues;
    }

    public void refreshAvailableVenues() {
        venueComboBox.getItems().setAll(getAvailableVenues());
    }
}