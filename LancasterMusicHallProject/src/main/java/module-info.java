module com.example.lancastermusichallproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens com.example.lancastermusichallproject to javafx.fxml;
    exports com.example.lancastermusichallproject;
}