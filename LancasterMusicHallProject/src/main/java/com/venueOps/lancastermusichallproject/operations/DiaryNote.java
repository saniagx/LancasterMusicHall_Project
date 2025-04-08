package com.venueOps.lancastermusichallproject.operations;

import java.time.LocalDate;

/**
 * Class that stores attributes for a Diary Note
 * @author Neil Daya
 * @version 1.0 April 6 2025
 */
public class DiaryNote {
    private LocalDate date;
    private String text;

    public DiaryNote(LocalDate date, String text) {
        this.date = date;
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
