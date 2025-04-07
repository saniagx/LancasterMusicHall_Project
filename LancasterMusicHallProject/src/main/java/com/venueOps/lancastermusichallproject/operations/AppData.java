package com.venueOps.lancastermusichallproject.operations;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppData {
    private static final List<String> VENUES = List.of("Main Hall", "Small Hall", "Rehearsal Space", "The Green Room",
            "Bronte Boardroom", "Dickens Den", "Poe Parlor", "Globe Room", "Chekhov Chamber");
    private static LocalDate selectedDate;
    private static ObservableList<String> companyNames = FXCollections.observableArrayList();
    private static List<IEvent> currentBookingEvents = new ArrayList<>();
    private static Map<String, String> notesMap = new HashMap<>();
    private static InvoiceInfo selectedInvoice;
    private static ContractInfo selectedContract;

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

    public static Map<String, String> getAllNotes() {
        return notesMap;
    }

    public static String getNote(String dateKey) {
        return notesMap.get(dateKey);
    }

    public static void saveNote(String dateKey, String noteText) {
        notesMap.put(dateKey, noteText);
    }

    public static void deleteNote(String dateKey) {
        notesMap.remove(dateKey);
    }

    public static void loadNotes(HashMap<String, String> diaryMap) {
        notesMap = diaryMap;
    }

    public static void setSelectedInvoice(InvoiceInfo invoice) {
        selectedInvoice = invoice;
    }
    public static void setSelectedContract(ContractInfo contract) {
        selectedContract = contract;
    }

    public static InvoiceInfo getSelectedInvoice() {
        return selectedInvoice;
    }
    public static ContractInfo getSelectedContract() {return selectedContract;}
}
