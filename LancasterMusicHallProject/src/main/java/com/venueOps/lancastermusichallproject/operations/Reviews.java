package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class Reviews {

    @FXML private TableView<Event> reviewTable;
    @FXML private TableColumn<Event, Integer> Booking_ID;
    @FXML private TableColumn<Event, Integer> Event_ID;
    @FXML private TableColumn<Event, String> Event_Name;
    @FXML private TableColumn<Event, Button> View_Button;

    @FXML
    private void initialize() {
        Booking_ID.setCellValueFactory(new PropertyValueFactory<>("bookingID"));
        Event_ID.setCellValueFactory(new PropertyValueFactory<>("eventID"));
        Event_Name.setCellValueFactory(new PropertyValueFactory<>("eventName"));

        View_Button.setCellValueFactory(cellData -> {
            Button button = new Button("View");
            button.setOnAction(e -> {
                AppData.setSelectedEvent(cellData.getValue());
                ReviewsPage controller = (ReviewsPage) ScreenController.getController("ReviewsPage");
                if (controller != null) {
                    controller.Refresh();
                }
                ScreenController.loadScreen("ReviewsPage");
            });
            return new SimpleObjectProperty<>(button);
        });

        populateReviewTable();
    }

    private void populateReviewTable() {
        List<Event> events = DatabaseConnection.getAllEvents(); // You should create this method
        reviewTable.setItems(FXCollections.observableArrayList(events));
    }

    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

    public void Refresh() {
        populateReviewTable();
    }
}
