module com.venueOps.lancastermusichallproject {
    requires javafx.fxml;

    requires java.sql;
    requires java.dotenv;
    requires com.calendarfx.view;

    opens com.venueOps.lancastermusichallproject to javafx.fxml;
    opens com.venueOps.lancastermusichallproject.operations to javafx.fxml;
    exports com.venueOps.lancastermusichallproject;
    exports com.venueOps.lancastermusichallproject.operations;
}