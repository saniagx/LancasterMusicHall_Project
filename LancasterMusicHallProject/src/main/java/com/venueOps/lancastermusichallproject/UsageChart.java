package com.venueOps.lancastermusichallproject;


import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

import java.sql.Ref;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class UsageChart extends Application{

    @FXML
    private TableView<String> venueTable;
    @FXML private TableColumn<String, String> venueColumn;
    @FXML private Canvas chartCanvas;
    @FXML private Canvas timelineCanvas;

    private final List<String> venues = List.of("Main Hall", "Small Hall", "Rehearsal Venue", "The Green Room", "Brontë Boardroom", "Dickens Den", "Poe Parlor", "Globe Room", "Chekhov Chamber");
    private GraphicsContext chart_gc;
    private GraphicsContext timeline_gc;
    private int cell_size = 50;

    LocalDate today;
    LocalDate currentMonday;
    LocalDate prevMonday;
    LocalDate nextMonday;
    List<LocalDate> weekStarts;

    public UsageChart() {}

    @FXML
    public void initialize() {
        // Setup venue list
        venueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        venueColumn.setMaxWidth(120);
        venueColumn.setPrefWidth(TableView.USE_COMPUTED_SIZE);
        venueColumn.setSortable(false);
        venueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        venueTable.setRowFactory(tv -> new TableRow<String>() { // Set row height
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setPrefHeight(cell_size);
            }
        });
        venueTable.getItems().addAll(venues);

        chart_gc = chartCanvas.getGraphicsContext2D();
        timeline_gc = timelineCanvas.getGraphicsContext2D();

        today = LocalDate.now();
        currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        prevMonday = currentMonday.minusWeeks(1);
        nextMonday = currentMonday.plusWeeks(1);
        weekStarts = List.of(prevMonday, currentMonday, nextMonday);

        drawChartBase();
        Refresh();
    }

    // Draws Checkerboard Pattern
    private void drawChartBase() {
        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 9; j++) {
                if ((i + j) % 2 == 0) {
                    chart_gc.setFill(Color.WHITE);
                } else {
                    chart_gc.setFill(Color.LIGHTGRAY);
                }
                chart_gc.fillRect(cell_size*i, cell_size*j, cell_size, cell_size);
            }
        }
    }

    private void drawTimeline() {
        //timeline_gc.clearRect(0, 0, 1049, 64);
        double dayWidth = cell_size;

        timeline_gc.setFill(Color.WHITE);
        timeline_gc.fillRect(0, 0, timelineCanvas.getWidth(), 39);
        for (int i = 0; i < 22; i++) {
            if (i % 2 == 0) {
                timeline_gc.setFill(Color.LIGHTGRAY);
            } else {
                timeline_gc.setFill(Color.WHITE);
            }
            timeline_gc.fillRect(cell_size*i, 39, cell_size, 24);
        }

        // Draw horizontal separators
        timeline_gc.setStroke(Color.BLACK);
        timeline_gc.strokeLine(0, 0, 1049, 0);
        timeline_gc.strokeLine(0, 39, 1049, 39);

        for (LocalDate weekStart : weekStarts) {
            //System.out.println(weekStart);
            double weekX = ChronoUnit.DAYS.between(prevMonday, weekStart) * dayWidth;

            // Draw week label
            timeline_gc.setFill(Color.BLACK);
            timeline_gc.fillText(weekStart.toString(), weekX + 5, 34);

            // Draw vertical separator for weeks
            timeline_gc.strokeLine(weekX, 39, weekX, 0);

            // Draw days for current and previous week
            for (int j = 0; j < 7; j++) {
                LocalDate day = weekStart.plusDays(j);
                double dayX = weekX + (j * dayWidth);

                timeline_gc.fillText(String.valueOf(day.getDayOfMonth()), dayX + 5, 59);

                // Draw vertical separator for days
                timeline_gc.strokeLine(dayX, 64, dayX, 39);
            }
        }
    }

    // To do: Fetch list of events, draw a bar for each of them
    private void drawEvents() {

    }

    public void BackButton() {
        ScreenController.loadScreen("MainMenu");
    }

    public void changeWeek(int weeks) {
        currentMonday = currentMonday.plusWeeks(weeks);
        prevMonday = currentMonday.minusWeeks(1);
        nextMonday = currentMonday.plusWeeks(1);
        weekStarts = List.of(prevMonday, currentMonday, nextMonday);
        Refresh();
    }

    public void NextWeek() {
        changeWeek(1);
    }

    public void PrevWeek() {
        changeWeek(-1);
    }

    public void Today() {
        currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        changeWeek(0);
    }

    public void Refresh() {
        drawEvents();
        drawTimeline();
    }
}
