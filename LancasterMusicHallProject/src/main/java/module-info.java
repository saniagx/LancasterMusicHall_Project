module com.venueOps.lancastermusichallproject {
    requires javafx.fxml;

    requires java.sql;
    requires java.dotenv;
    requires com.calendarfx.view;
    requires kernel;
    requires layout;
    requires ical4j.core;

    opens com.venueOps.lancastermusichallproject to javafx.fxml;
    opens com.venueOps.lancastermusichallproject.operations to javafx.fxml;
    exports com.venueOps.lancastermusichallproject;
    exports com.venueOps.lancastermusichallproject.operations;
}