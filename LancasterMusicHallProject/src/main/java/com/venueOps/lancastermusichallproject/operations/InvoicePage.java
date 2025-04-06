package com.venueOps.lancastermusichallproject.operations;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.venueOps.lancastermusichallproject.ScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InvoicePage {
    @FXML private Label bookingName;
    @FXML private Label invoiceID;
    @FXML private Label dateIssued;
    @FXML private Label dueDate;
    @FXML private Label billingName;
    @FXML private Label billingAddress;
    @FXML private Label billingEmail;
    @FXML private Label totalPrice;
    @FXML private Button exportPDF;
    @FXML private TableView<VenueTable> venueTable;
    @FXML private TableColumn<VenueTable, String> venueColumn;
    @FXML private TableColumn<VenueTable, BigDecimal> priceColumn;

    @FXML
    private void initialize() {
        venueColumn.setCellValueFactory(new PropertyValueFactory<>("venueName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    @FXML
    public void BackButton() {
        ScreenController.loadScreen("Invoices");
    }

    @FXML
    public void exportPDF() {
        try {
            // define path
            String folderPath = "Invoices";
            java.io.File folder = new java.io.File(folderPath);

            // create the folder if it doesn't exist
            if (!folder.exists()) {
                folder.mkdirs();
            }


            // set PDF dest
            String dest = folderPath + "/Invoice_" + invoiceID.getText() + ".pdf";

            // write the pdf
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            String dateToday = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
            Text boldDateText = new Text("Date Exported: " + dateToday + "\n").setBold();
            Paragraph dateParagraph = new Paragraph(boldDateText).setTextAlignment(TextAlignment.LEFT);
            document.add(dateParagraph);

            document.add(new Paragraph("Invoice ID: " + invoiceID.getText()));
            document.add(new Paragraph("Booking Name: " + bookingName.getText()));
            document.add(new Paragraph("Date Issued: " + dateIssued.getText()));
            document.add(new Paragraph("Due Date: " + dueDate.getText()));
            document.add(new Paragraph("Client: " + billingName.getText()));
            document.add(new Paragraph("Email: " + billingEmail.getText()));
            document.add(new Paragraph("Address: " + billingAddress.getText()));
            document.add(new Paragraph("Total Price: " + totalPrice.getText()));
            document.add(new Paragraph("\nVenues:\n"));

            Table venueTablePDF = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();
            venueTablePDF.addHeaderCell("Venue Name");
            venueTablePDF.addHeaderCell("Price");

            for (VenueTable venue : venueTable.getItems()) {
                venueTablePDF.addCell(venue.getVenueName());
                venueTablePDF.addCell("£" + venue.getPrice().toString());
            }

            document.add(venueTablePDF);
            document.close();

            System.out.println("PDF exported to: " + dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}