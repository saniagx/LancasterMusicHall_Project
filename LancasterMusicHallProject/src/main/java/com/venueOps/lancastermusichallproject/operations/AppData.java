package com.venueOps.lancastermusichallproject.operations;

import java.time.LocalDate;

public class AppData {
    private static LocalDate selectedDate;

    public static void setSelectedDate(LocalDate selectedDate) {
        AppData.selectedDate = selectedDate;
    }

    public static LocalDate getSelectedDate() {
        return selectedDate;
    }
}
