package com.venueOps.lancastermusichallproject.marketing;

/**
 * Interface defining operations-related functionalities.
 */
public interface OperationsInterface {

    // Handles large event booking enquiries
    void handleLargeBookingEnquiry(int enquiryID, String eventName, String eventType,
                                   String eventDate, String startTime, String endTime,
                                   int venueID, String venueName, String bookingStatus);

    // Manages reserved seating for events
    void reserveSeats(String[] reservedSeatsList, int holdTime, String eventType);

    // Configures venue setup instructions
    void configureVenue(int venueID, String setupInstructions);

    // Applies and tracks discounts
    void applyDiscount(int discountID, String discountType, double discountAmount);
}
