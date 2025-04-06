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

    @FXML private TableView<Event> invoiceTable;
    // Attributes for individual fields in the table, to be set based on event data held

    @FXML private TableColumn<Event, Integer> Booking_ID;
    @FXML private TableColumn<Event, String> Event_Name;
    @FXML private TableColumn<Event, String> Client_Name;
    @FXML private TableColumn<Event, Button> Invoice;

    //Attribute to view the invoice, only visible when a corresponding event exists
    @FXML Button viewInvoice;

    private LocalDate today;
    private LocalDate currentMonday;
    private LocalDate prevMonday;
    private LocalDate nextMonday;
    private List<LocalDate> weekStarts;

    //(Sania: My Plan)
    //The page for invoice always exists. When the button is clicked, the page gets updated
    //with the corresponding details based on the specific event

    //Holds the events
    private ArrayList<Event> events;

    //constructor
    public Invoices(){}



    //initialise
    @FXML private void initialize() throws Exception {

        //setting date and time boundaries for number of rows to show
        today = LocalDate.now();
        currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        prevMonday = currentMonday.minusWeeks(1);
        nextMonday = currentMonday.plusWeeks(1);
        weekStarts = List.of(prevMonday, currentMonday, nextMonday);
        //Get Events for Usage Charts also works to get events for the invoices (as we are getting events over a time period)
        events = DatabaseConnection.getEventsForUsageChart(prevMonday, nextMonday.plusDays(6));

        Booking_ID.setCellValueFactory(new PropertyValueFactory<Event, Integer>("Booking ID"));
        Event_Name.setCellValueFactory(new PropertyValueFactory<Event, String>("Event Name"));
        Client_Name.setCellValueFactory(new PropertyValueFactory<Event, String>("Client Name"));
        Invoice.setCellValueFactory(new PropertyValueFactory<Event, Button>("Invoice"));

        //initialiseEvents();
    }

    //Need to write code to get events for invoices


//    public void NextWeek() {
//        changeWeek(1);
//    }
//
//    public void PrevWeek() {
//        changeWeek(-1);
//    }
//    public Event getEvent() {
//        return event;
//    }


    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

}
