package com.venueOps.lancastermusichallproject.operations;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppData {
    private static final List<String> VENUES = List.of("Main Hall", "Small Hall", "Rehearsal Space", "The Green Room",
            "Bronte Boardroom", "Dickens Den", "Poe Parlor", "Globe Room", "Chekhov Chamber");
    private static LocalDate selectedDate;
    private static ObservableList<String> companyNames = FXCollections.observableArrayList();
    private static List<IEvent> currentBookingEvents = new ArrayList<>();

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

    public static List<IEvent> getCurrentBookingEvents() {
        return currentBookingEvents;
    }

    public static void setCurrentBookingEvents(List<IEvent> events) {
        AppData.currentBookingEvents = currentBookingEvents;
    }

    public static void addEventToCurrentBookingEvents(IEvent event) {
        AppData.currentBookingEvents.add(event);
    }

    public static void removeEventFromCurrentBookingEvents(IEvent event) {
        AppData.currentBookingEvents.remove(event);
    }

    public static void clearCurrentBookingEvents() {
        AppData.currentBookingEvents.clear();
    }
}
