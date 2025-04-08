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
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Screen Controller class for the ContractPage screen
 * Shows all of a contract's details
 * @author Neil Daya
 * @author Sania Ghori
 * @version 2.0 April 7 2025
 */
public class ContractPage {

    @FXML Label booking_ID;

    @FXML private TableView<ContractInfo> bookingOverviewTable;

    @FXML private TableColumn<ContractInfo, String> booking_Name;
    @FXML private TableColumn<ContractInfo, LocalDate> start_Time;
    @FXML private TableColumn<ContractInfo, LocalDate> end_Time;
    @FXML private TableColumn<ContractInfo, BigDecimal> total_Price;
    @FXML private TableColumn<ContractInfo, String> status;

    @FXML private TableView<ContractInfo> clientOverviewTable;

    @FXML private TableColumn<ContractInfo, String> client_Company;
    @FXML private TableColumn<ContractInfo, String> client_Name;
    @FXML private TableColumn<ContractInfo, String> client_Tel;
    @FXML private TableColumn<ContractInfo, String> client_Email;

    /**
     * FXML initialiser method
     * Populates the screen with contract information
     */
    @FXML
    private void initialize() {
        booking_Name.setCellValueFactory(new PropertyValueFactory<>("bookingName"));
        start_Time.setCellValueFactory(new PropertyValueFactory<>("bookingStartDate"));
        end_Time.setCellValueFactory(new PropertyValueFactory<>("bookingEndDate"));
        total_Price.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        client_Company.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        client_Name.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        client_Tel.setCellValueFactory(new PropertyValueFactory<>("clientTelephone"));
        client_Email.setCellValueFactory(new PropertyValueFactory<>("clientEmail"));

        ContractInfo contract = AppData.getSelectedContract();
        if (contract != null) {
            Refresh();
        }
    }

    public void Refresh() {
        ContractInfo contract = AppData.getSelectedContract();
        if (contract == null) return;

        booking_ID.setText(String.valueOf(contract.getBookingID()));

        ObservableList<ContractInfo> eventData = FXCollections.observableArrayList(contract);
        bookingOverviewTable.setItems(eventData);

        ObservableList<ContractInfo> clientData = FXCollections.observableArrayList(contract);
        clientOverviewTable.setItems(clientData);
    }

    @FXML
    public void BackButton() {
        ScreenController.loadScreen("Contracts");
    }

    /**
     * Exports the contract to PDF
     */
    public void exportPDF() {
        try {
            ContractInfo contract = AppData.getSelectedContract();
            // define path
            String folderPath = "Contracts";
            java.io.File folder = new java.io.File(folderPath);

            // create the folder if it doesn't exist
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // set PDF dest
            int counter = 1;
            String baseName = "Contract_" + contract.getBookingID();
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

            document.add(new Paragraph("Booking ID: " + contract.getBookingID()));
            document.add(new Paragraph("Booking Name: " + contract.getBookingName()));
            document.add(new Paragraph("Start Date: " + contract.getBookingStartDate()));
            document.add(new Paragraph("End Date: " + contract.getBookingEndDate()));
            document.add(new Paragraph("Total Price: " + contract.getTotalPrice()));
            document.add(new Paragraph("Status: " + contract.getStatus()));
            document.add(new Paragraph("Company: " + contract.getCompanyName()));
            document.add(new Paragraph("Client: " + contract.getClientName()));
            document.add(new Paragraph("Telephone: " + contract.getClientTelephone()));
            document.add(new Paragraph("Email: " + contract.getClientEmail()));

            document.close();
            sendAlert("Export Successful", "The contract has been exported as a PDF.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResource("/com/venueOps/lancastermusichallproject/assets/lancastercirclelogo.png").toExternalForm()));
        alert.showAndWait();
    }
}
