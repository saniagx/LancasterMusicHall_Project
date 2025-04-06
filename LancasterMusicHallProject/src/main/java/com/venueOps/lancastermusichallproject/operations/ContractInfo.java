package com.venueOps.lancastermusichallproject.operations;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContractInfo {

    //related to the booking
    private int bookingID;
    private String bookingName;

    //the bottom 3 need to be added to the UI somehow (maybe client ID is not needed)
    private int contractID;
    private int clientID;
    private LocalDate signedDate;

    //also related to the booking
    private BigDecimal totalPrice;
    private String status;

    //related to the client
    private String companyName;
    private String clientName;
    private String clientTelephone;
    private String clientEmail;

    public ContractInfo(int bookingID, String bookingName, int contractID, int clientID, LocalDate signedDate, BigDecimal totalPrice, String status, String companyName, String clientName, String clientTelephone, String clientEmail) {
        this.bookingID = bookingID;
        this.bookingName = bookingName;
        this.contractID = contractID;
        this.clientID = clientID;
        this.signedDate = signedDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.companyName = companyName;
        this.clientName = clientName;
        this.clientTelephone = clientTelephone;
        this.clientEmail = clientEmail;
    }

    public int getBookingID() {
        return bookingID;
    }

    public String getBookingName() {
        return bookingName;
    }

    public int getContractID() {
        return contractID;
    }

    public int getClientID() {
        return clientID;
    }

    public LocalDate getSignedDate() {
        return signedDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientTelephone() {
        return clientTelephone;
    }

    public String getClientEmail() {
        return clientEmail;
    }
}

