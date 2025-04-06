package com.venueOps.lancastermusichallproject.operations;


import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

//Class to view all the existing Contracts for LMH
public class Contracts {

    @FXML
    private TableView<IEvent> contractTable;
    // Attributes for individual fields in the table, to be set based on event data held

    @FXML private TableColumn<Event, Integer> Booking_ID;
    @FXML private TableColumn<Event, String> Event_Name;
    @FXML private TableColumn<Event, String> Client_Name;
    @FXML private TableColumn<Event, Button> Contract;

    //Attribute to view the contract, only visible when a corresponding event exists
    @FXML Button viewContracts;

    //Holds the events
    private ArrayList<IEvent> events;

    public Contracts(){}
    //initialise
    @FXML private void initialize() throws Exception {

        //we only need these four attributes/columns for events in contracts
        Booking_ID.setCellValueFactory(new PropertyValueFactory<Event, Integer>("Booking ID"));
        Event_Name.setCellValueFactory(new PropertyValueFactory<Event, String>("Event Name"));
        Client_Name.setCellValueFactory(new PropertyValueFactory<Event, String>("Client Name"));
        Contract.setCellValueFactory(new PropertyValueFactory<Event, Button>("Contract"));

        //populate the contract table with all the right data
        populateContractTable();

        //when the table row button is clicked, the code should open the corresponding page
    }

    //display the correct contract based on booking id.


    //populate the contract table with events
    public void populateContractTable() {
        contractTable.getItems().clear();
        List<Event> events = DatabaseConnection.getEventsForInvoicesAndContracts();
        contractTable.getItems().addAll(events);
    }
    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }
}
