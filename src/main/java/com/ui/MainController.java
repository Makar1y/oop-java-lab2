package com.ui;

import com.loan.AnnuityLoan;
import com.loan.LinearLoan;
import com.loan.Loan;
import com.loan.PaymentData;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private TextField amountField;

    @FXML
    private DatePicker startField;

    @FXML
    private TextField monthsField;

    @FXML
    private TextField interestField;

    @FXML
    private ComboBox<String> typeBox;

    @FXML
    private TextField deferStartField;

    @FXML
    private TextField deferDurationField;

    @FXML
    private TextField deferInterestField;

    @FXML
    private TextField filterFromField;

    @FXML
    private TextField filterToField;

    @FXML
    private TableView<PaymentData> paymentsTable;

    @FXML
    private TableColumn<PaymentData, String> colDate;

    @FXML
    private TableColumn<PaymentData, Double> colTotal;

    @FXML
    private TableColumn<PaymentData, Double> colPrincipal;

    @FXML
    private TableColumn<PaymentData, Double> colInterest;

    @FXML
    private TableColumn<PaymentData, Double> colBalance;

    @FXML
    private VBox resultBox;

    private List<PaymentData> masterData = new java.util.ArrayList<>();

    @FXML
    public void initialize() {
        typeBox.getItems().addAll("Anuiteto", "Linijinis");
        typeBox.getSelectionModel().selectFirst();

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPayment"));
        colPrincipal.setCellValueFactory(new PropertyValueFactory<>("principal"));
        colInterest.setCellValueFactory(new PropertyValueFactory<>("interest"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
    }

    @FXML
    protected void onCalculateClick() {
        try {
            masterData = calculateData("Linijinis".equals(typeBox.getValue()));

            paymentsTable.getItems().clear();
            paymentsTable.getItems().addAll(masterData);

        } catch (Exception e) {
            System.err.println("Input format error: " + e.getMessage());
        }
    }

    @FXML
    protected void onFilterClick() {
        try {
            int from = filterFromField.getText().isEmpty() ? 0 : Integer.parseInt(filterFromField.getText());
            int to = filterToField.getText().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(filterToField.getText());

            List<PaymentData> filtered = new java.util.ArrayList<>();
            for (int i = 0; i < masterData.size(); ++i) {
                int absoluteMonth = i + 1;
                if (absoluteMonth >= from && absoluteMonth <= to) {
                    filtered.add(masterData.get(i));
                }
            }
            paymentsTable.getItems().clear();
            paymentsTable.getItems().addAll(filtered);
        } catch (Exception e) {
            System.err.println("Filter error: " + e.getMessage());
        }
    }

    @FXML
    protected void onSaveReportClick() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("report.csv"))) {
            writer.println("Data,Imoka,Pagrindas,Palukanos,Likutis");
            for (PaymentData p : paymentsTable.getItems()) {
                writer.printf("%s;%.2f;%.2f;%.2f;%.2f\n",
                        p.getDate(), p.getTotalPayment(),
                        p.getPrincipal(), p.getInterest(), p.getBalance());
            }
            System.out.println("Report saved to report.csv");
        } catch (IOException e) {
            System.err.println("Save error: " + e.getMessage());
        }
    }

    @FXML
    protected void onShowChartClick() {
        if (amountField.getText().isEmpty() || monthsField.getText().isEmpty() || interestField.getText().isEmpty()) {
            return;
        }

        List<PaymentData> linData = calculateData(true);
        List<PaymentData> anData = calculateData(false);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Mėnuo (Bendra trukmė)");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Suma");
        yAxis.setForceZeroInRange(false);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Mokėjimų Grafikas");
        chart.setCreateSymbols(false);

        XYChart.Series<Number, Number> linSeries = new XYChart.Series<>();
        linSeries.setName("Linijinis");
        int month = 0;
        for (PaymentData p : linData) {
            linSeries.getData().add(new XYChart.Data<>(++month, p.getTotalPayment()));
        }

        XYChart.Series<Number, Number> anSeries = new XYChart.Series<>();
        anSeries.setName("Anuiteto");
        month = 0;
        for (PaymentData p : anData) {
            anSeries.getData().add(new XYChart.Data<>(++month, p.getTotalPayment()));
        }

        chart.getData().addAll(linSeries, anSeries);

        Scene scene = new Scene(chart, 600, 400);
        Stage stage = new Stage();
        stage.setTitle("Grafikas");
        stage.setScene(scene);
        stage.show();
    }

    protected List<PaymentData> calculateData(boolean linear) {
        double amount = Double.parseDouble(amountField.getText().replace(",", "."));
        int totalMonths = monthsField.getText().isEmpty() ? 0 : Integer.parseInt(monthsField.getText());
        double interest = Double.parseDouble(interestField.getText().replace(",", "."));

        Loan loan;
        if (linear) {
            loan = new LinearLoan(amount, totalMonths, interest);
        } else {
            loan = new AnnuityLoan(amount, totalMonths, interest);
        }

        if (!deferStartField.getText().isEmpty()) {
            int defStart = Integer.parseInt(deferStartField.getText());
            int defDuration = Integer
                    .parseInt(deferDurationField.getText().isEmpty() ? "0" : deferDurationField.getText());
            double defInterest = Double.parseDouble(deferInterestField.getText().isEmpty() ? "0.0"
                    : deferInterestField.getText().replace(",", "."));
            loan.setDeferral(defStart, defDuration, defInterest);
        }

        java.time.LocalDate startDate = startField.getValue();
        return loan.calculateSchedule(startDate);
    }
}
