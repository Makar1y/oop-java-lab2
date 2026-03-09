package com.loan;

import java.time.LocalDate;
import java.util.ArrayList;

public class AnnuityLoan extends Loan {
    protected String loanType = "Annuity Loan";

    public AnnuityLoan(double amount, int totalMonths, double percentage) {
        super(amount, totalMonths, percentage);
    }

    @Override
    public ArrayList<PaymentData> calculateSchedule(LocalDate startDate) {
        ArrayList<PaymentData> schedule = new ArrayList<>();
        double currentBalance = this.amount;
        int activeMonths = calculateActiveMonths();

        for (int m = 1; m <= this.totalMonths; ++m) {
            boolean isDeferred = (deferStartMonth > 0 &&
                    m >= deferStartMonth &&
                    m < deferStartMonth + deferDuration);

            double interestPortion;
            double principalPortion;
            double totalPayment;

            if (isDeferred) {
                interestPortion = currentBalance * this.deferRate;
                principalPortion = 0;
                totalPayment = interestPortion;
            } else {

                if (this.monthlyRate > 0) {
                    totalPayment = currentBalance * (monthlyRate * Math.pow(1 + monthlyRate, activeMonths)) / (Math.pow(1 + monthlyRate, activeMonths) - 1);
                    interestPortion = currentBalance * monthlyRate;
                    principalPortion = totalPayment - interestPortion;
                } else {
                    principalPortion = currentBalance / activeMonths;
                    interestPortion = 0;
                    totalPayment = principalPortion;
                }

                if (m == this.totalMonths || principalPortion > currentBalance) {
                    principalPortion = currentBalance;
                    totalPayment = principalPortion + interestPortion;
                }

                currentBalance -= principalPortion;
                --activeMonths;
            }

            addPayment(schedule, startDate, m, totalPayment, principalPortion, interestPortion, currentBalance);
        }

        return schedule;
    }

    @Override
    public String getLoanType() {
        return this.loanType;
    }
}
