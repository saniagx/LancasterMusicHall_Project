package com.venueOps.lancastermusichallproject.operations;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.util.Date;

public class EventOverviewTable {

    private final String booking_Name;
    private final Date start_Time;
    private final Date end_Time;
    private final BigDecimal total_Price;
    private final String status;

    public EventOverviewTable(String booking_Name, Date start_Time, Date end_Time, BigDecimal total_Price, String status) {
        this.booking_Name = booking_Name;
        this.start_Time = start_Time;
        this.end_Time = end_Time;
        this.total_Price = total_Price;
        this.status = status;
    }

    public String getBooking_Name() {
        return booking_Name;
    }

    public Date getStart_Time() {
        return start_Time;
    }

    public Date getEnd_Time() {
        return end_Time;
    }

    public BigDecimal getTotal_Price() {
        return total_Price;
    }

    public String getStatus() {
        return status;
    }
}
