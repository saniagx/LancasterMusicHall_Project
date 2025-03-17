package com.example.lancastermusichallproject.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeatingConfig implements ISeatingConfig {
    private static Map<String, Integer> predefinedConfigs = new HashMap<>();
    private int seatingConfigID;
    private int venueID;
    private String eventType;
    private ArrayList<Section> sections;

    // Predefine seating configurations
    static {
        predefinedConfigs.put("1_LivePerformance", 101);
        predefinedConfigs.put("1_Film", 102);
        predefinedConfigs.put("2_Conference", 201);
    }

    // Retrieve SeatingConfigID based on venueID + eventType
    public static int getSeatingConfigID(int venueID, String eventType) {
        String key = venueID + "_" + eventType;
        return predefinedConfigs.getOrDefault(key, -1);
    }

    // Constructor
    public SeatingConfig(int venueID, String eventType) {
        this.venueID = venueID;
        this.eventType = eventType;
        this.seatingConfigID = getSeatingConfigID(venueID, eventType); // returns -1 if key doesn't exist
        this.sections = generateSeatingLayout(seatingConfigID);
    }

    // Generate Sections for an Event
    private ArrayList<Section> generateSeatingLayout(int seatingConfigID) {
        ArrayList<Section> sections = new ArrayList<>();

        if (seatingConfigID == 101) { // Live Performance
            sections.add(new Section("Stalls", 10, 20, 50.0f));
            sections.add(new Section("Balcony", 5, 15, 75.0f));
        }
        else if (seatingConfigID == 102) { // Film in Main Hall
            sections.add(new Section("Stalls", 12, 18, 30.0f));
        }
        else if (seatingConfigID == 201) { // Conference in Small Hall
            sections.add(new Section("Conference Room", 5, 10, 20.0f));
        }

        return sections;
    }

    // Get sections for given seatingConfigID
    @Override
    public ArrayList<Section> getSections(int seatingConfigID) {
        return sections;
    }

    // Get the SeatingConfig Object
    @Override
    public SeatingConfig getSeatingConfiguration(int seatingConfigID) {
        return this;
    }

    // **Retrieve Section Name**
    @Override
    public String getSectionName(Section section) {
        return section.getSectionName();
    }

    @Override
    public void setSectionName(Section section, String sectionName) {
        section.setSectionName(sectionName);
    }

    // Retrieve All Seats in a Section
    @Override
    public ArrayList<Seat> getSeats(Section section) {
        return section.getSeats();
    }

    // Get Restricted View Seats
    @Override
    public ArrayList<Seat> getRestrictedSeats(Section section) {
        ArrayList<Seat> restrictedSeats = new ArrayList<>();
        for (Seat seat : section.getSeats()) {
            if (seat.isRestrictedView()) {
                restrictedSeats.add(seat);
            }
        }
        return restrictedSeats;
    }

    // Modify Seat Booking Status
    @Override
    public void setSeatBooked(Seat seat, boolean booked) {
        seat.setBooked(booked);
    }

    @Override
    public void setSeatVip(Seat seat, boolean vip) {
        seat.setVip(vip);
    }

    @Override
    public void setSeatRestrictedView(Seat seat, boolean restricted) {
        seat.setRestrictedView(restricted);
    }

    // Commented out for now - need additional information
//    @Override
//    public int getSeatNumber(Seat seat) {
//        return seat.getSeatNum();
//    }
//
//    @Override
//    public void setSeatNumber(Seat seat, int seatNumber) {
//        seat.setSeatNum(seatNumber);
//    }
//
//    @Override
//    public int getRowNumber(Seat seat) {
//        return seat.getRowNumber();
//    }
//
//    @Override
//    public void setRowNumber(Seat seat, int rowNumber) {
//        seat.setRowNumber(rowNumber);
//    }
//
//    @Override
//    public Section getSeatSection(Seat seat) {
//        return seat.getSection();
//    }
//
//    @Override
//    public void setSeatSection(Seat seat, String section) {
//        // This shouldn't be modified - but keeping for now in case it can be
//    }
//
//    @Override
//    public float getPrice(Seat seat) {
//        return seat.getPrice();
//    }
//
//    @Override
//    public void setPrice(Seat seat, float price) {
//        seat.setPrice(price);
//    }
//
//    @Override
//    public int getBookingID(Seat seat) {
//        return seat.getBookingID();
//    }
//
//    @Override
//    public void setBookingID(Seat seat, int bookingID) {
//        seat.setBookingID(bookingID);
//    }
//
//    @Override
//    public boolean isBooked(Seat seat) {
//        return seat.isBooked();
//    }
//
//    @Override
//    public void setBooked(Seat seat, boolean booked) {
//        seat.setBooked(booked);
//    }

}
