package com.venueOps.lancastermusichallproject.operations;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;

public class AppData {
    private static final List<String> VENUES = List.of("Main Hall", "Small Hall", "Rehearsal Space", "The Green Room",
            "Bronte Boardroom", "Dickens Den", "Poe Parlor", "Globe Room", "Chekhov Chamber");
    private static LocalDate selectedDate;
    private static ObservableList<String> companyNames = FXCollections.observableArrayList();
    private static Booking currentBooking;

    public static List<String> getVenues() { return VENUES; }

    public static LocalDate getSelectedDate() {
        return selectedDate;
    }

    public static void setSelectedDate(LocalDate selectedDate) {
        AppData.selectedDate = selectedDate;
    }

    public static ObservableList<String> getCompanyNames() {
        return companyNames;
    }

    public static void setCompanyNames(ObservableList<String> companyNames) {
        AppData.companyNames = companyNames;
    }

    public static Booking getCurrentBooking() {
        return currentBooking;
    }

    public static void setCurrentBooking(Booking currentBooking) {
        AppData.currentBooking = currentBooking;
    }
}
