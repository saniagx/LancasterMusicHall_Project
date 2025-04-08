package com.venueOps.lancastermusichallproject.operations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Class that stores attributes for a booking
 * @author Neil Daya
 * @version 2.0 April 6 2025
 */
public class Booking {
    private int bookingID;
    private String bookingName;
    private List<IEvent> events;
    private Client client;
    private LocalDate signedDate;
    private BigDecimal totalPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    /**
     * Constructor for storing booking details in the database
     * @param bookingName name of the booking
     * @param events list of events for the booking
     * @param client client who made the booking
     * @param signed_date date booking was made
     * @param total_price total price of the booking
     * @param start_date start date of the first event
     * @param end_date end date of the last event
     * @param status confirmed / completed / cancelled
     */
    public Booking(String bookingName, List<IEvent> events, Client client, LocalDate signed_date, BigDecimal total_price,
                   LocalDate start_date, LocalDate end_date, String status) {
        this.bookingName = bookingName;
        this.bookingID = 0; // Placeholder until Database assigns it
        this.events = events;
        this.client = client;
        this.signedDate = signed_date;
        this.totalPrice = total_price;
        this.startDate = start_date;
        this.endDate = end_date;
        this.status = status;
    }

    /**
     * Constructor for loading booking details from the database
     * @param bookingID unique identifier assigned by the database
     * @param bookingName name of the booking
     * @param events list of events for the booking
     * @param client client who made the booking
     * @param signed_date date booking was made
     * @param total_price total price of the booking
     * @param start_date start date of the first event
     * @param end_date end date of the last event
     * @param status confirmed / completed / cancelled
     */
    public Booking(int bookingID, String bookingName, List<IEvent> events, Client client, LocalDate signed_date, BigDecimal total_price,
                   LocalDate start_date, LocalDate end_date, String status) {
        this.bookingName = bookingName;
        this.bookingID = bookingID;
        this.events = events;
        this.client = client;
        this.signedDate = signed_date;
        this.totalPrice = total_price;
        this.startDate = start_date;
        this.endDate = end_date;
        this.status = status;
    }

    public int getBookingID() {return bookingID;}
    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
        // All events in the same booking have the same bookingID
        for (IEvent event : events) {
            event.setBookingID(bookingID);
        }
    }

    public String getBookingName() {return bookingName;}
    public void setBookingName(String bookingName) {this.bookingName = bookingName;}

    public List<IEvent> getEvents() {return events;}
    public void setEvents(List<IEvent> events) {this.events = events;}

    public Client getClient() {return client;}
    public void setClient(Client client) {this.client = client;}

    public LocalDate getSignedDate() {return signedDate;}
    public void setSignedDate(LocalDate signed_date) {this.signedDate = signed_date;}

    public BigDecimal getTotalPrice() {return totalPrice;}
    public void setTotalPrice(BigDecimal total_price) {this.totalPrice = total_price;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate start_date) {this.startDate = start_date;}

    public LocalDate getEndDate() {return endDate;}
    public void setEnd_date(LocalDate end_date) {this.endDate = end_date;}

    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}

}
