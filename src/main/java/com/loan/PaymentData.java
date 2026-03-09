package com.loan;

public class PaymentData {
    private String date;
    private double totalPayment;
    private double principal;
    private double interest;
    private double balance;

    public PaymentData(String date, double totalPayment, double principal, double interest, double balance) {
        this.date = date;
        this.totalPayment = totalPayment;
        this.principal = principal;
        this.interest = interest;
        this.balance = balance;
    }

    public String getDate() {
        return date;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public double getPrincipal() {
        return principal;
    }

    public double getInterest() {
        return interest;
    }

    public double getBalance() {
        return balance;
    }
}
