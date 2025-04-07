package com.venueOps.lancastermusichallproject.operations;

import java.time.LocalDate;

public class ReviewsInfo {
    private int bookingId;
    private int eventId;
    private String reviewerName;
    private int rating;
    private String comment;
    private LocalDate datePosted;

    public ReviewsInfo(int bookingId, int eventId, String reviewerName, int rating, String comment, LocalDate datePosted) {
        this.bookingId = bookingId;
        this.eventId = eventId;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
        this.datePosted = datePosted;
    }

    public int getBookingId() { return bookingId; }
    public int getEventId() { return eventId; }
    public String getReviewerName() { return reviewerName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDate getDatePosted() { return datePosted; }
}
