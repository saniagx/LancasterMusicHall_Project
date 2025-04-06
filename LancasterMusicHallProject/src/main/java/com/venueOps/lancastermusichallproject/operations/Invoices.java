package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.fortuna.ical4j.validate.PropertyValidator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;


//Class to view all the existing Invoices for LMH
public class Invoices {

    @FXML private TableView<InvoiceInfo> invoiceTable;
    // Attributes for individual fields in the table, to be set based on event data held

    @FXML private TableColumn<InvoiceInfo, Integer> Booking_ID;
    @FXML private TableColumn<InvoiceInfo, String> Event_Name;
    @FXML private TableColumn<InvoiceInfo, String> Client_Name;
    @FXML private TableColumn<InvoiceInfo, Button> Invoice;

    //Attribute to view the invoice, only visible when a corresponding event exists
    @FXML Button viewInvoice;

    //Holds the events
    private ArrayList<IEvent> events;

    //constructor
    public Invoices(){}

    //initialise
    @FXML private void initialize() throws Exception {

        //we only need these four attributes/columns for events in invoices
        System.out.println("Booking_ID is: " + Booking_ID);
        Booking_ID.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        Event_Name.setCellValueFactory(new PropertyValueFactory<>("eventNames"));
        Client_Name.setCellValueFactory(new PropertyValueFactory<>("clientName"));

        Invoice.setCellValueFactory(cellData -> {
            Button button = new Button("View");
            button.setOnAction(e -> {
                AppData.setSelectedInvoice(cellData.getValue());
                // Load screen and call refresh method
                InvoicePage controller = (InvoicePage) ScreenController.getController("InvoicePage");
                if (controller != null) {
                    controller.Refresh();
                }

                ScreenController.loadScreen("InvoicePage");
            });
            return new SimpleObjectProperty<>(button);
        });
        //populate the invoice table with all the right data
        populateInvoiceTable();

        //when the table row button is clicked, the code should open the corresponding page
    }

    //display the correct invoice based on booking id.


    //populate the invoice table with events
    public void populateInvoiceTable() {
        List<InvoiceInfo> invoiceList = DatabaseConnection.getInvoices();
        invoiceTable.setItems(FXCollections.observableArrayList(invoiceList));
    }

    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

    public void refreshInvoices() {
        populateInvoiceTable();
    }

}
