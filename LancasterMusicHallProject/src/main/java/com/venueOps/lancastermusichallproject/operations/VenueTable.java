package com.venueOps.lancastermusichallproject.operations;

import java.math.BigDecimal;

/**
 * Class that stores information about a Venue
 * @author Meer Ali
 * @version 1.0 April 6 2025
 */
public class VenueTable {
    private final String venueName;
    private final BigDecimal price;

    public VenueTable(String venueName, BigDecimal price) {
        this.venueName = venueName;
        this.price = price;
    }

    public String getVenueName() {
        return venueName;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
