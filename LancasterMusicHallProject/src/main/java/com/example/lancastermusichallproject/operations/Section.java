package com.example.lancastermusichallproject.operations;

import java.util.ArrayList;

// These are section objects (named after Main Hall, Meeting Room etc.)
// Which can contain their own array of seats

public class Section {

    // Declare Section Name
    private String sectionName;
    private ArrayList<Seat> seats = new ArrayList<Seat>();;
    private int numRows;
    private int seatsPerRow;

    // Constructor
    public Section(String sectionName, int numRows, int seatsPerRow, float basePrice) {
        this.sectionName = sectionName;
        this.numRows = numRows;
        this.seatsPerRow = seatsPerRow;
        this.seats = new ArrayList<>();

        // Generate seats
        for (int row = 1; row <= numRows; row++) {
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                seats.add(new Seat(seatNum, row, this, basePrice));
            }
        }
    }

    // Constructor
    public Section(String sectionName){
        this.sectionName = sectionName;
    }

    // Getter and Setter for sectionName
    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    // Getter for seats
    public ArrayList<Seat> getSeats() {
        return seats;
    }

    // Adding seat manually
    public void addSeat(Seat seat) {
        if (!seats.contains(seat)) {
            seats.add(seat);
        }
    }

    // Remove a seat manually
    public void removeSeat(Seat seat) {
        seats.remove(seat);
    }

    // Getter for Number of Rows
    public int getNumRows() {
        return numRows;
    }

    // Getter for Seats Per Row
    public int getSeatsPerRow() {
        return seatsPerRow;
    }


}
