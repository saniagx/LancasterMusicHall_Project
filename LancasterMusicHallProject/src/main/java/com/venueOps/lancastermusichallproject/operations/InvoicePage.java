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
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;
import javafx.collections.FXCollections;
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
import java.util.List;

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

        InvoiceInfo invoice = AppData.getSelectedInvoice();
        if (invoice != null) {
            loadInvoiceDetails(invoice); // load details
        }
    }

    @FXML
    public void BackButton() {
        ScreenController.loadScreen("Invoices");
    }

    @FXML
    public void exportPDF() {
        try {
            Refresh();
            InvoiceInfo invoice = AppData.getSelectedInvoice();
            // define path
            String folderPath = "Invoices";
            java.io.File folder = new java.io.File(folderPath);

            // create the folder if it doesn't exist
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // set PDF dest
            int counter = 1;
            String baseName = "Invoice_" + invoice.getInvoiceId();
            String dest = folderPath + "/" + baseName + ".pdf";
            java.io.File file = new java.io.File(dest);

            while (file.exists()) {
                dest = folderPath + "/" + baseName + "(" + counter++ + ").pdf";
                file = new java.io.File(dest);
            }

            // write the pdf
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            String dateToday = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
            Text boldDateText = new Text("Date Exported: " + dateToday + "\n").setBold();
            Paragraph dateParagraph = new Paragraph(boldDateText).setTextAlignment(TextAlignment.LEFT);
            document.add(dateParagraph);

            document.add(new Paragraph("Invoice ID: " + invoice.getInvoiceId()));
            document.add(new Paragraph("Booking Name: " + invoice.getEventNames()));
            document.add(new Paragraph("Date Issued: " + invoice.getIssueDate()));
            document.add(new Paragraph("Due Date: " + invoice.getDueDate()));
            document.add(new Paragraph("Client: " + invoice.getClientName()));
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

    public void Refresh() {
        InvoiceInfo invoice = AppData.getSelectedInvoice();
        if (invoice == null) return;

        bookingName.setText(invoice.getEventNames());
        invoiceID.setText(String.valueOf(invoice.getInvoiceId()));
        dateIssued.setText(invoice.getIssueDate().toString());
        dueDate.setText(invoice.getDueDate().toString());
        billingName.setText(invoice.getClientName());

        // fetch billing info
        Client client = DatabaseConnection.getClientDetailsByBookingId(invoice.getBookingId());
        if (client != null) {
            billingAddress.setText(client.getAddress() + ", " + client.getCity() + ", " + client.getPostcode());
            billingEmail.setText(client.getEmail());
        }

        // total price
        totalPrice.setText("£" + invoice.getTotalPrice().toPlainString());

        // fetch venue list
        List<VenueTable> venueList = DatabaseConnection.getVenuesForInvoice(invoice.getBookingId());
        venueTable.getItems().setAll(venueList);
    }

    public void loadInvoiceDetails(InvoiceInfo invoice) {
        // Set details
        invoiceID.setText(String.valueOf(invoice.getInvoiceId()));
        bookingName.setText("(Booking Name Here)");
        dateIssued.setText(invoice.getIssueDate().toString());
        dueDate.setText(invoice.getDueDate().toString());
        billingName.setText(invoice.getClientName());


        // fetch venues from DB based on invoice's bookingId
        List<VenueTable> venues = DatabaseConnection.getVenuesForInvoice(invoice.getBookingId());
        venueTable.setItems(FXCollections.observableArrayList(venues));
    }



}