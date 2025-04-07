package com.venueOps.lancastermusichallproject.operations;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.venueOps.lancastermusichallproject.ScreenController;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReviewsPage {

    @FXML private TableView<ReviewsInfo> reviewsTable;
    @FXML private TableColumn<ReviewsInfo, String> Reviewer_Name;
    @FXML private TableColumn<ReviewsInfo, Integer> Rating;
    @FXML private TableColumn<ReviewsInfo, String> Comment;
    @FXML private TableColumn<ReviewsInfo, LocalDate> Date_Posted;
    @FXML private Button exportPDF;

    @FXML
    private void initialize() {
        Reviewer_Name.setCellValueFactory(new PropertyValueFactory<>("reviewerName"));
        Rating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        Comment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        Date_Posted.setCellValueFactory(new PropertyValueFactory<>("datePosted"));

        Refresh();
    }

    public void Refresh() {
        Event selectedEvent = AppData.getSelectedEvent();
        if (selectedEvent == null) return;

        List<ReviewsInfo> reviews = DatabaseConnection.getReviewsForEvent(
                selectedEvent.getBookingID(), selectedEvent.getEventID()
        );
        reviewsTable.setItems(FXCollections.observableArrayList(reviews));
    }

    @FXML
    public void BackButton() {
        ScreenController.loadScreen("Reviews");
    }

    @FXML
    public void exportPDF() {
        try {
            Event event = AppData.getSelectedEvent();
            if (event == null) return;

            String folderPath = "Reviews";
            java.io.File folder = new java.io.File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            String fileName = "Reviews_B" + event.getBookingID() + "_E" + event.getEventID() + ".pdf";
            String dest = folderPath + "/" + fileName;
            int counter = 1;
            while (new java.io.File(dest).exists()) {
                dest = folderPath + "/" + fileName.replace(".pdf", "(" + counter++ + ").pdf");
            }

            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            String dateToday = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
            document.add(new Paragraph("Date Exported: " + dateToday + "\n"));
            document.add(new Paragraph("Event: " + event.getEventName()));
            document.add(new Paragraph("Booking ID: " + event.getBookingID()));
            document.add(new Paragraph("Event ID: " + event.getEventID()));
            document.add(new Paragraph("\nReviews:\n"));

            Table table = new Table(UnitValue.createPercentArray(new float[]{20, 10, 50, 20})).useAllAvailableWidth();
            table.addHeaderCell("Reviewer");
            table.addHeaderCell("Rating");
            table.addHeaderCell("Comment");
            table.addHeaderCell("Date");

            for (ReviewsInfo review : reviewsTable.getItems()) {
                table.addCell(review.getReviewerName());
                table.addCell(String.valueOf(review.getRating()));
                table.addCell(review.getComment());
                table.addCell(review.getDatePosted().toString());
            }

            document.add(table);
            document.close();

            System.out.println("PDF exported to: " + dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
