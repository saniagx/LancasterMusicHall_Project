package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
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

    @FXML private TableView<IEvent> invoiceTable;
    // Attributes for individual fields in the table, to be set based on event data held

    @FXML private TableColumn<Event, Integer> Booking_ID;
    @FXML private TableColumn<Event, String> Event_Name;
    @FXML private TableColumn<Event, String> Client_Name;
    @FXML private TableColumn<Event, Button> Invoice;

    //Attribute to view the invoice, only visible when a corresponding event exists
    @FXML Button viewInvoice;

    //Attributes to view details on the Invoice Page
    @FXML Label bookingName;

    @FXML Label invoiceID;
    @FXML Label dateIssued;
    @FXML Label dueDate;

    @FXML Label billingName;
    @FXML Label billingAddress;
    @FXML Label billingEmail;

    @FXML Label totalPrice;

    @FXML Button exportPDF;

    //Holds the events
    private ArrayList<IEvent> events;

    //constructor
    public Invoices(){}



    //initialise
    @FXML private void initialize() throws Exception {

        //we only need these four attributes/columns for events in invoices
        Booking_ID.setCellValueFactory(new PropertyValueFactory<Event, Integer>("Booking ID"));
        Event_Name.setCellValueFactory(new PropertyValueFactory<Event, String>("Event Name"));
        Client_Name.setCellValueFactory(new PropertyValueFactory<Event, String>("Client Name"));
        Invoice.setCellValueFactory(new PropertyValueFactory<Event, Button>("Invoice"));

        //populate the invoice table with all the right data
        populateInvoiceTable();

        //when the table row button is clicked, the code should open the corresponding page
    }

    //display the correct invoice based on booking id.


    //populate the invoice table with events
    public void populateInvoiceTable() {
        invoiceTable.getItems().clear();
        List<Event> events = DatabaseConnection.getEventsForInvoicesAndContracts();
        invoiceTable.getItems().addAll(events);
    }

    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

    public void exportPDF() {
        //needs editing
        ScreenController.loadScreen("MainMenu");
    }

}
