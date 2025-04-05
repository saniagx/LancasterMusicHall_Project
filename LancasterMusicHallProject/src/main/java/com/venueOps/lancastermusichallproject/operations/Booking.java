package com.venueOps.lancastermusichallproject.operations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Booking {
    private int bookingID;
    private List<IEvent> events;
    private Client client;
    private LocalDate signed_date;
    private BigDecimal total_price;
    private LocalDate start_date;
    private LocalDate end_date;
    private String status; // Confirmed - Cancelled - Completed

    public Booking(List<IEvent> events,Client client, LocalDate signed_date, BigDecimal total_price,
                   LocalDate start_date, LocalDate end_date, String status) {
        this.bookingID = 0; // Placeholder until Database assigns it
        this.events = events;
        this.client = client;
        this.signed_date = signed_date;
        this.total_price = total_price;
        this.start_date = start_date;
        this.end_date = end_date;
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

    public List<IEvent> getEvents() {return events;}
    public void setEvents(List<IEvent> events) {this.events = events;}

    public Client getClient() {return client;}
    public void setClient(Client client) {this.client = client;}

    public LocalDate getSignedDate() {return signed_date;}
    public void setSignedDate(LocalDate signed_date) {this.signed_date = signed_date;}

    public BigDecimal getTotalPrice() {return total_price;}
    public void setTotalPrice(BigDecimal total_price) {this.total_price = total_price;}

    public LocalDate getStartDate() {return start_date;}
    public void setStartDate(LocalDate start_date) {this.start_date = start_date;}

    public LocalDate getEndDate() {return end_date;}
    public void setEnd_date(LocalDate end_date) {this.end_date = end_date;}

    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
}
