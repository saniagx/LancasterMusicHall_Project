package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingsOverview {
    // Bookings tab attributes
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Integer> bookingIDColumn;
    @FXML private TableColumn<Booking, String> companyNameColumn;
    @FXML private TableColumn<Booking, String> contactNameColumn;
    @FXML private TableColumn<Booking, String> emailColumn;
    @FXML private TableColumn<Booking, String> phoneColumn;
    @FXML private TableColumn<Booking, LocalDate> signedDateColumn;
    @FXML private TableColumn<Booking, LocalDate> startDateColumn;
    @FXML private TableColumn<Booking, LocalDate> endDateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    private List<IEvent> events;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML public void initialize() {
        bookingIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBookingID()).asObject());
        companyNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClient().getCompanyName()));
        contactNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClient().getContactFirstName() + " " + cellData.getValue().getClient().getContactLastName()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClient().getEmail()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClient().getPhone()));
        signedDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSignedDate()));
        startDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStartDate()));
        endDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndDate()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        populateBookingsTable();

        bookingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                events = newSelection.getEvents();
                refresh();
            }
        });
    }

    public void populateBookingsTable() {
        bookingsTable.getItems().clear();
        List<Booking> bookings = DatabaseConnection.getBookings();
        bookingsTable.getItems().addAll(bookings);
    }

    // Events tab attributes
    @FXML private Tab eventsTab;
    @FXML private GridPane eventsGridPane;

    public void refresh() {
        eventsTab.setDisable(false);
        drawEventsGrid();
    }

    private void drawEventsGrid() {
        eventsGridPane.getChildren().clear();
        eventsGridPane.getColumnConstraints().clear();
        eventsGridPane.getRowConstraints().clear();

        int columns = 3;
        // Add cards to the grid
        int row = 0;
        int col = 0;
        for (IEvent event : events) {
            AnchorPane card = createEventCard(event);
            eventsGridPane.add(card, col, row);

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }

    private AnchorPane createEventCard(IEvent event) {
        AnchorPane card = new AnchorPane();
        card.setPrefSize(240, 120);
        card.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10px; -fx-border-color: #122023; -fx-border-radius: 10px;");
        Font boldFont = Font.font("System", FontWeight.BOLD, 12);
        Font regularFont = Font.font("System", FontWeight.NORMAL, 12);

        // Create Labels for each event detail
        int textOffset = 5;

        Label nameLabel = new Label("Name: ");
        nameLabel.setFont(boldFont);
        nameLabel.setLayoutX(10);
        nameLabel.setLayoutY(10);
        Label nameLabelValue = new Label(event.getEventName());
        nameLabelValue.setFont(regularFont);
        nameLabel.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            nameLabelValue.setLayoutX(nameLabel.getLayoutX() + newWidth.doubleValue() + textOffset);
        });
        nameLabelValue.setLayoutY(10);

        Label typeLabel = new Label("Type:");
        typeLabel.setFont(boldFont);
        typeLabel.setLayoutX(10);
        typeLabel.setLayoutY(30);
        Label typeLabelValue = new Label(event.getEventType());
        typeLabelValue.setFont(regularFont);
        typeLabel.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            typeLabelValue.setLayoutX(typeLabel.getLayoutX() + newWidth.doubleValue() + textOffset);
        });
        typeLabelValue.setLayoutY(30);

        Label startLabel = new Label("Start:");
        startLabel.setFont(boldFont);
        startLabel.setLayoutX(10);
        startLabel.setLayoutY(50);
        Label startLabelValue = new Label(event.getEventStart().format(formatter));
        startLabelValue.setFont(regularFont);
        startLabel.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            startLabelValue.setLayoutX(startLabel.getLayoutX() + newWidth.doubleValue() + textOffset);
        });
        startLabelValue.setLayoutY(50);

        Label endLabel = new Label("End:");
        endLabel.setFont(boldFont);
        endLabel.setLayoutX(160);
        endLabel.setLayoutY(50);
        Label endLabelValue = new Label(event.getEventEnd().format(formatter));
        endLabelValue.setFont(regularFont);
        endLabel.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            endLabelValue.setLayoutX(endLabel.getLayoutX() + newWidth.doubleValue() + textOffset);
        });
        endLabelValue.setLayoutY(50);

        Label priceLabel = new Label("Ticket Price:");
        priceLabel.setFont(boldFont);
        priceLabel.setLayoutX(10);
        priceLabel.setLayoutY(70);
        Label priceLabelValue = new Label(String.format("£%.2f", event.getTicketPrice()));
        priceLabelValue.setFont(regularFont);
        priceLabel.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            priceLabelValue.setLayoutX(priceLabel.getLayoutX() + newWidth.doubleValue() + textOffset);
        });
        priceLabelValue.setLayoutY(70);

        Label discountLabel = new Label("Max Discount:");
        discountLabel.setFont(boldFont);
        discountLabel.setLayoutX(160);
        discountLabel.setLayoutY(70);
        Label discountLabelValue = new Label(String.format("%.2f%%", event.getMaxDiscount()));
        discountLabelValue.setFont(regularFont);
        discountLabel.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            discountLabelValue.setLayoutX(discountLabel.getLayoutX() + newWidth.doubleValue() + textOffset);
        });
        discountLabelValue.setLayoutY(70);

        Label venueLabel = new Label("Venue:");
        venueLabel.setFont(boldFont);
        venueLabel.setLayoutX(10);
        venueLabel.setLayoutY(90);
        Label venueLabelValue = new Label(event.getVenueName());
        venueLabelValue.setFont(regularFont);
        venueLabel.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            venueLabelValue.setLayoutX(venueLabel.getLayoutX() + newWidth.doubleValue() + textOffset);
        });
        venueLabelValue.setLayoutY(90);

        card.getChildren().addAll(nameLabel, nameLabelValue, typeLabel, typeLabelValue, startLabel, startLabelValue, endLabel,
                endLabelValue, priceLabel, priceLabelValue, discountLabel, discountLabelValue, venueLabel, venueLabelValue);
        return card;
    }

    public void BackButton() {
        ScreenController.loadScreen("Calendar");
    }

    public void CancelBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showError("No booking selected");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Cancel Booking");
        VBox content = new VBox(10);
        Label messageLabel = new Label();
        content.getChildren().add(messageLabel);
        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        if ("Cancelled".equals(selectedBooking.getStatus())) {
            messageLabel.setText("This booking is already cancelled");
            dialog.getDialogPane().getButtonTypes().clear();
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        } else {
            messageLabel.setText("Are you sure you want to cancel Booking " + selectedBooking.getBookingID() + "?");
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButton) {
                    try {
                        DatabaseConnection.updateBookingStatus(selectedBooking.getBookingID(), "Cancelled");
                        selectedBooking.setStatus("Cancelled");
                        bookingsTable.refresh();
                        messageLabel.setText("Booking " + selectedBooking.getBookingID() + " has been cancelled");
                    } catch (SQLException e) {
                        messageLabel.setText("Failed to cancel booking: " + e.getMessage());
                    }
                    dialog.getDialogPane().getButtonTypes().clear();
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    return null;
                }
                return dialogButton; // Close dialog
            });
        }

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();

        try {
            DatabaseConnection.updateBookingStatus(selectedBooking.getBookingID(), "Cancelled");
            selectedBooking.setStatus("Cancelled");
            bookingsTable.refresh();
        } catch (SQLException e) {
            showError("Failed to cancel booking: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
