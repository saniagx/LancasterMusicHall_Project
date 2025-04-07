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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Class to view all the existing Contracts for LMH
public class Contracts {

    @FXML private TableView<ContractInfo> contractTable;
    // Attributes for individual fields in the table, to be set based on event data held

    @FXML private TableColumn<ContractInfo, Integer> Booking_ID;
    @FXML private TableColumn<ContractInfo, String> Booking_Name;
    @FXML private TableColumn<ContractInfo, String> Client_Name;
    @FXML private TableColumn<ContractInfo, Button> Contract;

    //Attribute to view the contract, only visible when a corresponding event exists
    @FXML Button viewContracts;

    //Attributes to view details on the Individual Contracts Page
    @FXML Label booking_ID;

    @FXML Button exportPDF;

    //Holds the events
    private ArrayList<IEvent> events;

    public Contracts(){}
    //initialise
    @FXML private void initialize() throws Exception {

        //we only need these four attributes/columns for events in contracts
        Booking_ID.setCellValueFactory(new PropertyValueFactory<>("bookingID"));
        Booking_Name.setCellValueFactory(new PropertyValueFactory<>("bookingName"));
        Client_Name.setCellValueFactory(new PropertyValueFactory<>("clientName"));


        Contract.setCellValueFactory(cellData -> {
            Button button = new Button("View");
            button.setOnAction(e -> {
                AppData.setSelectedContract(cellData.getValue());
                ContractPage controller = (ContractPage) ScreenController.getController("ContractPage");
                if (controller != null) {
                    controller.Refresh();
                }

                ScreenController.loadScreen("ContractPage");
            });
            return new SimpleObjectProperty<>(button);
        });
        //populate the contract table with all the right data
        populateContractTable();
    }



    //populate the contract table with events
    public void populateContractTable() {
        List<ContractInfo> contractList = DatabaseConnection.getContracts();
        contractTable.setItems(FXCollections.observableArrayList(contractList));
    }
    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

    public void exportPDF() {
        //needs editing
        ScreenController.loadScreen("MainMenu");
    }

}
