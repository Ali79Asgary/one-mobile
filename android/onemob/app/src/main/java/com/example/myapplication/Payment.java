package com.example.myapplication;

public class Payment {

    private int paymentAmount;
    private int paymentCost;

    public Payment(int paymentAmount, int paymentCost) {
        this.paymentAmount = paymentAmount;
        this.paymentCost = paymentCost;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(int paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public int getPaymentCost() {
        return paymentCost;
    }

    public void setPaymentCost(int paymentCost) {
        this.paymentCost = paymentCost;
    }
}
