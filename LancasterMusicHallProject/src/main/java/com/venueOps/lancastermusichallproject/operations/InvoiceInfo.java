package com.venueOps.lancastermusichallproject.operations;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceInfo {
    private int invoiceId;
    private int bookingId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal totalPrice;
    private String eventNames;
    private String clientName;
    private String clientAddress;
    private String clientCity;
    private String clientPostcode;
    private String clientEmail;

    public InvoiceInfo(int invoiceId, int bookingId, LocalDate issueDate, LocalDate dueDate, BigDecimal totalPrice, String eventNames,
                       String clientName, String clientAddress, String clientCity, String clientPostcode, String clientEmail) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.totalPrice = totalPrice;
        this.eventNames = eventNames;
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.clientCity = clientCity;
        this.clientPostcode = clientPostcode;
        this.clientEmail = clientEmail;
    }

    // Getters
    public int getInvoiceId() { return invoiceId; }
    public int getBookingId() { return bookingId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getEventNames() { return eventNames; }
    public String getClientName() { return clientName; }
    public String getClientAddress() { return clientAddress; }
    public String getClientCity() { return clientCity; }
    public String getClientPostcode() { return clientPostcode; }
    public String getClientEmail() { return clientEmail; }
}
