package com.venueOps.lancastermusichallproject.operations;

import java.util.ArrayList;
import java.util.List;

public interface ISeatingConfig{

    int getSeatingConfigID();
    void setSeatingConfigID(int seatingConfigID);

    int getCapacity();
    void setCapacity(int capacity);

    String getLayout();
    void setLayout(String layout);

    String getVenueName();
    void setVenueName(String venueName);

    List<String> getRestrictedViews();
    void setRestrictedViews(List<String> restrictedViews);

    // Get sections for a given seating configuration ID
    //ArrayList<Section> getSections(int seatingConfigID);

    // Get the full SeatingConfig object (if needed)
    //SeatingConfig getSeatingConfiguration(int seatingConfigID);

    // Get and set section names
    //String getSectionName(Section section);
    //void setSectionName(Section section, String sectionName);

    // Retrieve seats from a section
    //ArrayList<Seat> getSeats(Section section);
    //ArrayList<Seat> getRestrictedSeats(Section section);

    // Modify seat status
    //void setSeatBooked(Seat seat, boolean booked);
    //void setSeatVip(Seat seat, boolean vip);
    //void setSeatRestrictedView(Seat seat, boolean restricted);

    // Commented out for now - need additional information
//    int getSeatNumber(Seat seat);
//    void setSeatNumber(Seat seat, int seatNumber);
//
//    int getRowNumber(Seat seat);
//    void setRowNumber(Seat seat, int rowNumber);
//
//    Section getSeatSection(Seat seat);
//    void setSeatSection(Seat seat, String section);
//
//    float getPrice(Seat seat);
//    void setPrice(Seat seat, float price);
//
//    int getBookingID(Seat seat);
//    void setBookingID(Seat seat, int bookingID);
//
//    boolean isBooked(Seat seat);
//    void setBooked(Seat seat, boolean booked);
}
