package com.venueOps.lancastermusichallproject.operations;

/**
 * @Deprecated
 * Class which stores attributes about a seat
 * @author Neil Daya
 * @author Sania Ghori
 * @author Meer Ali
 * @version 2.0 March 3 2025
 */
public class Seat {

    //attributes for each seat
    private int seatNum;
    private int rowNum;
    private Section seatSection;
    private float price;
    private int bookingID;
    private boolean booked;
    private boolean restrictedView;
    private boolean vip;


    //constructor
    public Seat(int seatNum, int rowNum, Section seatSection, float price){
        this.seatNum = seatNum;
        this.rowNum = rowNum;
        this.seatSection = seatSection;
        this.price = price;
        this.bookingID = -1; // -1 to show seats by default aren't booked
        this.booked = false;
        this.restrictedView = false;
        this.vip = false;
    }

    // Getter and Setter for seatNum
    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNumber) {
        this.seatNum = seatNumber;
    }

    // Getter and Setter for rowNum
    public int getRowNumber() {
        return rowNum;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNum = rowNumber;
    }

    // Getter and Setter for seatSection
    public Section getSection() {
        return seatSection;
    }

    public void setSection(Section section) {
        this.seatSection = section;
    }

    // Getter and Setter for price
    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    // Getter and Setter for bookingID
    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    // Getter and Setter for booked
    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    // Getter and Setter for restrictedView
    public boolean isRestrictedView() {
        return restrictedView;
    }

    public void setRestrictedView(boolean restrictedView) {
        this.restrictedView = restrictedView;
    }

    // Getter and Setter for VIP
    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

}

