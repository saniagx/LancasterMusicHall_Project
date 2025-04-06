package com.venueOps.lancastermusichallproject.operations;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;

public class InvoicePage {
    @FXML private Label bookingName;
    @FXML private Label invoiceID;
    @FXML private Label dateIssued;
    @FXML private Label dueDate;
    @FXML private Label billingName;
    @FXML private Label billingAddress;
    @FXML private Label billingEmail;
    @FXML private Label totalPrice;
    @FXML private Button exportPDF;
    @FXML private TableView<VenueTable> venueTable;
    @FXML private TableColumn<VenueTable, String> venueColumn;
    @FXML private TableColumn<VenueTable, BigDecimal> priceColumn;

    @FXML
    private void initialize() {
        venueColumn.setCellValueFactory(new PropertyValueFactory<>("venueName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    public void BackButton(ActionEvent actionEvent) {
    }

    public void exportPDF(ActionEvent actionEvent) {
    }
}