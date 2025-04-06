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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class ContractPage {

    @FXML Label booking_ID;

    @FXML private TableView<VenueTable> eventOverviewTable;

    @FXML private TableColumn<VenueTable, String> bookingNameColumn;
    @FXML private TableColumn<VenueTable, Date> startTimeColumn;
    @FXML private TableColumn<VenueTable, Date> endTimeColumn;
    @FXML private TableColumn<VenueTable, BigDecimal> totalPriceColumn;
    @FXML private TableColumn<VenueTable, String> statusColumn;

    @FXML private TableView<VenueTable> clientOverviewTable;

    @FXML private TableColumn<VenueTable, String> companyNameColumn;
    @FXML private TableColumn<VenueTable, String> clientNameColumn;
    @FXML private TableColumn<VenueTable, String> clientTelColumn;
    @FXML private TableColumn<VenueTable, String> clientEmailColumn;


    @FXML
    private void initialize() {
        bookingNameColumn.setCellValueFactory(new PropertyValueFactory<>("booking_Name"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("start_Time"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("end_Time"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("total_Price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status_Column"));

        companyNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientCompanyName"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        clientTelColumn.setCellValueFactory(new PropertyValueFactory<>("clientTel"));
        clientEmailColumn.setCellValueFactory(new PropertyValueFactory<>("clientEmail"));
    }


    @FXML
    public void BackButton() {
        ScreenController.loadScreen("Contracts");
    }





}
