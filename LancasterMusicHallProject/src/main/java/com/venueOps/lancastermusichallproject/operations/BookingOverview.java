package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BookingOverview {
    // Events tab attributes
    @FXML private GridPane eventsGridPane;
    private ArrayList<IEvent> events;

    // Client Details tab attributes
    @FXML private TextField companyNameField;
    @FXML private TextField contactFNameField;
    @FXML private TextField contactLNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField postcodeField;
    @FXML private ListView<String> clientListView;
    private ObservableList<String> allCompanies; // Full list from database
    private ObservableList<String> filteredCompanies; // Filtered list for ListView

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML public void initialize() {
        events = new ArrayList<>();
        refresh();
        
        filteredCompanies = FXCollections.observableArrayList();

        filteredCompanies.addAll(allCompanies);
        clientListView.setItems(filteredCompanies);

        // Changes list view depending on what user types in company name field
        companyNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterCompanies(newVal);
        });

        // Fills text fields depending on selected client
        clientListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateFields(newVal);
            }
        });
    }

    public void refresh() {
        drawEventsGrid();
        allCompanies = DatabaseConnection.getCompanyNames();
        AppData.setCompanyNames(allCompanies);

    }

    public void addEventToList(IEvent event) {
        events.add(event);
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
        Label priceLabelValue = new Label(String.format("£%.2f", event.getEventPrice()));
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

        // Delete Button
        Button deleteButton = new Button("Delete");
        deleteButton.setPrefSize(70, 16);
        card.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            deleteButton.setLayoutX(newWidth.doubleValue() - deleteButton.getPrefWidth() - 5);
        });
        deleteButton.setLayoutY(5);
        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-background-radius: 10px; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setOnAction(e -> {
            events.remove(event);
            drawEventsGrid(); // Redraw grid after removal
        });

        card.getChildren().addAll(nameLabel, nameLabelValue, typeLabel, typeLabelValue, startLabel, startLabelValue, endLabel,
                endLabelValue, priceLabel, priceLabelValue, discountLabel, discountLabelValue, venueLabel, venueLabelValue, deleteButton);
        return card;
    }

    private void filterCompanies(String searchText) {
        filteredCompanies.clear();
        if (searchText == null || searchText.isEmpty()) {
            filteredCompanies.addAll(AppData.getCompanyNames());
        } else {
            String lowerSearchText = searchText.toLowerCase();
            for (String company : AppData.getCompanyNames()) {
                if (company.toLowerCase().contains(lowerSearchText)) {
                    filteredCompanies.add(company);
                }
            }
        }
    }

    private void populateFields(String companyName) {
        Client client = DatabaseConnection.getClientDetails(companyName);
        if (client != null) {
            companyNameField.setText(client.getCompanyName());
            contactFNameField.setText(client.getContactFirstName());
            contactLNameField.setText(client.getContactLastName());
            emailField.setText(client.getEmail());
            phoneField.setText(client.getPhone());
            addressField.setText(client.getAddress());
            cityField.setText(client.getCity());
            postcodeField.setText(client.getPostcode());
        }
    }

    public void BackButton() {
        events.clear();
        ScreenController.loadScreen("DayOverview");
    }

    public void AddEvent() { ScreenController.loadScreen("AddEvent"); }

    public void ClearClientFields() {
        clientListView.getSelectionModel().clearSelection();
        companyNameField.setText("");
        contactFNameField.setText("");
        contactLNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        cityField.setText("");
        postcodeField.setText("");
    }

    public void ConfirmBooking() { ScreenController.loadScreen("Invoice"); }
}
