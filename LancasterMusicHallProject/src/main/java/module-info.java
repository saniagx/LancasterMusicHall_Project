module com.venueOps.lancastermusichallproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires java.dotenv;

    opens com.venueOps.lancastermusichallproject to javafx.fxml;
    exports com.venueOps.lancastermusichallproject;
}