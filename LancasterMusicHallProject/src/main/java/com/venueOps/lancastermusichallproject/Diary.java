package com.venueOps.lancastermusichallproject;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Diary {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea noteTextArea;

    @FXML
    private ListView<String> notesListView;

    private final Map<String, String> notesMap = new HashMap<>(); // store notes by date

    public Diary() {}

    // Back to Main Menu calls mainMenuScreen within Application
    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

    // Add a new note
    public void addNote() {
        LocalDate selectedDate = datePicker.getValue();
        String noteText = noteTextArea.getText().trim();

        if (selectedDate == null) {
            Alert("Error", "Please select a date before adding a note");
            return;
        }

        if (noteText.isEmpty()) {
            Alert("Error", "Note cannot be empty");
            return;
        }

        String dateKey = selectedDate.toString();
        notesMap.put(dateKey, noteText);

        notesListView.getItems().add(dateKey + ": " + noteText); // Display note
        noteTextArea.clear(); // Clear input field
    }

    // Delete a note
    public void deleteNote() {
        String selectedNote = notesListView.getSelectionModel().getSelectedItem();

        if (selectedNote == null) {
            Alert("Error", "Please select a note to delete");
            return;
        }

        String dateKey = selectedNote.split(":")[0];
        notesMap.remove(dateKey);

        notesListView.getItems().remove(selectedNote); // Remove date from list
    }

    // Show alerts for errors (e.g. no date selected, empty note, no note selected to delete, etc.)
    private void Alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
