package com.venueOps.lancastermusichallproject.marketing;
import java.util.*;

/**
 * Implements OperationsInterface with basic method logic.
 */
public class OperationsManager implements OperationsInterface {

    private Map<Integer, String> largeBookings = new HashMap<>();
    private Map<String, Integer> reservedSeats = new HashMap<>();
    private Map<Integer, String> venueConfigurations = new HashMap<>();
    private Map<Integer, String> discounts = new HashMap<>();

    // Handles large event booking enquiries
    @Override
    public void handleLargeBookingEnquiry(int enquiryID, String eventName, String eventType,
                                          String eventDate, String startTime, String endTime,
                                          int venueID, String venueName, String bookingStatus) {
        String bookingDetails = eventName + " (" + eventType + ") on " + eventDate +
                " from " + startTime + " to " + endTime + " at " + venueName + " - " + bookingStatus;
        largeBookings.put(enquiryID, bookingDetails);
        System.out.println("Large Booking Added: " + bookingDetails);
    }

    // Reserves seats for an event
    @Override
    public void reserveSeats(String[] reservedSeatsList, int holdTime, String eventType) {
        for (String seat : reservedSeatsList) {
            reservedSeats.put(seat, holdTime);
        }
        System.out.println("Reserved Seats for " + eventType + ": " + Arrays.toString(reservedSeatsList));
    }

    // Configures venue setup instructions
    @Override
    public void configureVenue(int venueID, String setupInstructions) {
        venueConfigurations.put(venueID, setupInstructions);
        System.out.println("Venue Configured: " + setupInstructions);
    }

    // Applies and tracks discounts
    @Override
    public void applyDiscount(int discountID, String discountType, double discountAmount) {
        String discountDetails = discountType + " Discount: £" + discountAmount;
        discounts.put(discountID, discountDetails);
        System.out.println("Discount Applied: " + discountDetails);
    }
}
