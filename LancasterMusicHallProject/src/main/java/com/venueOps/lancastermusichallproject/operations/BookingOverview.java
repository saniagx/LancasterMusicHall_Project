package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;

public class BookingOverview {
    public void refresh() {

    }

    public void BackButton() {
        ScreenController.loadScreen("DayOverview");
    }

    public void AddEvent() { ScreenController.loadScreen("AddEvent");
    }
    public void ConfirmBooking() { ScreenController.loadScreen("Invoice"); }
}
