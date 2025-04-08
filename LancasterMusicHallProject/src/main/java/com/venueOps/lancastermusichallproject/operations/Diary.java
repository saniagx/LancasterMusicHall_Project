package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Screen Controller for the Diary
 * Lets the user write notes and store them in the database
 * @author Meer Ali
 * @author Neil Daya
 * @author Sania Ghori
 * @version 5.0 April 6 2025
 */
public class Diary {
    @FXML private Label dateLabel;

    @FXML
    private TextArea noteTextArea;

    private LocalDate date;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    public Diary() {}

    @FXML
    public void initialize() {
        dateLabel.setText("Date");
    }


    // Back to Main Menu calls mainMenuScreen within Application
    public void BackButton() {
        ScreenController.loadScreen("Calendar");
    }

    /**
     * Adds a new note to the database
     */
    public void addNote() {
        LocalDate selectedDate = AppData.getSelectedDate();
        String noteText = noteTextArea.getText().trim();

        if (selectedDate == null) {
            Alert("Error", "Please select a date before adding a note");
            return;
        }

        if (noteText.isEmpty()) {
            Alert("Error", "Note cannot be empty");
            return;
        }

        String dateKey = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        AppData.saveNote(dateKey, noteText);
        DatabaseConnection.saveDiaryNote(new DiaryNote(selectedDate, noteText));

        //refresh
        Calendar calendarController = (Calendar) ScreenController.getController("Calendar");
        if (calendarController != null) {
            calendarController.refreshCalendar();
        }

        ScreenController.loadScreen("Calendar");
    }

    /**
     * Deletes a note from the database
     */
    public void deleteNote() {
        LocalDate selectedDate = AppData.getSelectedDate();

        if (selectedDate == null) {
            Alert("Error", "No date selected");
            return;
        }

        String dateKey = selectedDate.toString();
        String existingNote = AppData.getNote(dateKey);

        if (existingNote == null || existingNote.isEmpty()) { // checks if note exists before deleting
            Alert("Error", "No note exists for this date");
            return;
        }

        AppData.deleteNote(dateKey);
        DatabaseConnection.deleteNote(new DiaryNote(selectedDate, existingNote));
        noteTextArea.clear();

        //refresh
        Calendar calendarController = (Calendar) ScreenController.getController("Calendar");
        if (calendarController != null) {
            calendarController.refreshCalendar();
        }
        ScreenController.loadScreen("Calendar");
    }

    // Show alerts for errors (e.g. no date selected, empty note, no note selected to delete, etc.)
    private void Alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refresh() {
        date = AppData.getSelectedDate();
        dateLabel.setText(date.format(formatter));

        String existingNote = AppData.getNote(date.toString());
        if (existingNote != null) {
            noteTextArea.setText(existingNote);
        } else {
            noteTextArea.clear();
        }
    }
}
