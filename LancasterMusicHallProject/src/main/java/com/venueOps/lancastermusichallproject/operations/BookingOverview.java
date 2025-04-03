package com.venueOps.lancastermusichallproject.operations;

import com.venueOps.lancastermusichallproject.ScreenController;

public class BookingOverview {

    public void BackButton() {
        ScreenController.loadScreen("DayOverview");
    }

    public void EventSeating() { ScreenController.loadScreen("EventSeating"); }

    public void ConfirmBooking() { ScreenController.loadScreen("Invoice"); }
}
