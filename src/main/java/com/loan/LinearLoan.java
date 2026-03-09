package com.loan;

import java.time.LocalDate;
import java.util.ArrayList;

public class LinearLoan extends Loan {
    protected String loanType = "Linear Loan";

    public LinearLoan(double amount, int totalMonths, double percentage) {
        super(amount, totalMonths, percentage);
    }

    @Override
    public ArrayList<PaymentData> calculateSchedule(LocalDate startDate) {
        ArrayList<PaymentData> schedule = new ArrayList<>();
        double currentBalance = this.amount;
        int activeMonths = calculateActiveMonths();

        double fixedPrincipal = this.amount / activeMonths;

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
                interestPortion = currentBalance * this.monthlyRate;
                principalPortion = fixedPrincipal;

                if (principalPortion > currentBalance || m == this.totalMonths) {
                    principalPortion = currentBalance;
                }
                totalPayment = principalPortion + interestPortion;

                currentBalance -= principalPortion;
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
