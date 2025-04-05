package com.venueOps.lancastermusichallproject.operations;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class AppData {
    private static LocalDate selectedDate;
    private static ObservableList<String> companyNames = FXCollections.observableArrayList();
    private static Booking currentBooking;

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
