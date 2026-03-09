package com.loan;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.LocalDate;

public class Loan {
    protected double amount;
    protected int totalMonths;
    protected double percentage;

    double monthlyRate;
    double deferRate;

    protected String loanType = "Generic Loan";

    protected int deferStartMonth = -1;
    protected int deferDuration = 0;
    protected double deferInterestRate = 0.0;

    public Loan(double amount, int totalMonths, double percentage) {
        this.amount = amount;
        this.totalMonths = totalMonths;
        this.percentage = percentage;

        monthlyRate = (percentage / 100.0) / 12.0;
    }

    public void setDeferral(int deferStartMonth, int deferDuration, double deferInterestRate) {
        this.deferStartMonth = deferStartMonth;
        this.deferDuration = deferDuration;
        this.deferInterestRate = deferInterestRate;

        deferRate = (deferInterestRate / 100.0) / 12.0;
    }

    public String getLoanType() {
        return loanType;
    }

    protected int calculateActiveMonths() {
        int activeMonths = this.totalMonths;
        if (this.deferStartMonth > 0 && this.deferDuration > 0) {
            int endDefer = Math.min(this.deferStartMonth + this.deferDuration - 1, this.totalMonths);
            if (endDefer >= this.deferStartMonth)
                activeMonths -= (endDefer - this.deferStartMonth + 1);
        }
        if (activeMonths <= 0) {
            activeMonths = 1;
        }
        return activeMonths;
    };

    protected void addPayment( ArrayList<PaymentData> schedule, LocalDate startDate, int m, double totalPayment, double principalPortion, double interestPortion, double currentBalance) {
        String dateStr = (startDate != null) ? startDate.plusMonths(m).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "Mėn " + m;
        schedule.add(new PaymentData(
                dateStr,
                round(totalPayment),
                round(principalPortion),
                round(interestPortion),
                round(currentBalance)));
    };

    public ArrayList<PaymentData> calculateSchedule(LocalDate startDate) {
        return null;
    };

    protected static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
