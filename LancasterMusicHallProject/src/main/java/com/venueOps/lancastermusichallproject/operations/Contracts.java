package com.venueOps.lancastermusichallproject.operations;


import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Class to view all the existing Contracts for LMH
public class Contracts {

    @FXML private TableView<IEvent> contractTable;
    // Attributes for individual fields in the table, to be set based on event data held

    @FXML private TableColumn<Event, Integer> Booking_ID;
    @FXML private TableColumn<Event, String> Event_Name;
    @FXML private TableColumn<Event, String> Client_Name;
    @FXML private TableColumn<Event, Button> Contract;

    //Attribute to view the contract, only visible when a corresponding event exists
    @FXML Button viewContracts;

    //Attributes to view details on the Individual Contracts Page
    @FXML Label booking_ID;

    //Attributes for Table on Individual Contracts Page
    //Attributes for eventOverviewTable
    @FXML private TableView<IEvent> eventOverviewTable;
    @FXML private TableColumn<Event, Integer> booking_Name;
    @FXML private TableColumn<Event, Date> start_Time;
    @FXML private TableColumn<Event, Date> end_Time;
    @FXML private TableColumn<Event, String> total_Price;
    @FXML private TableColumn<Event, Button> status;
    //Attributes for clientOverviewTable
    @FXML private TableView<IEvent> clientOverviewTable;
    @FXML private TableColumn<Event, Integer> client_Company;
    @FXML private TableColumn<Event, String> client_Name;
    @FXML private TableColumn<Event, String> client_Tel;
    @FXML private TableColumn<Event, Button> client_Email;

    @FXML Button exportPDF;

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

    public void exportPDF() {
        //needs editing
        ScreenController.loadScreen("MainMenu");
    }

}
