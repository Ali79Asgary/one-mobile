package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.JsonPackage.JsonBuyCoin;
import com.example.myapplication.JsonPackage.JsonGetBalance;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {

    ListView paymentListView;
    TextView lblPaymentAmount;
    TextView lblPaymentCost;
    int[] paymentsAmount;
    int[] paymentsCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        try {
            setupUI();
            updateBalance();
            paymentsAmount = new int[]{1, 2, 5, 10, 15, 20, 30, 50, 100, 200, 300, 500};
            paymentsCost = new int[]{10000, 20000, 50000, 100000, 150000, 200000, 300000, 500000, 1000000, 2000000, 3000000, 5000000};
            makePayments(paymentListView, paymentsAmount, paymentsCost);
            updateBalance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupUI() {
        try {
            paymentListView = findViewById(R.id.paymentListView);
            lblPaymentAmount = findViewById(R.id.lblPaymentAmount);
            lblPaymentCost = findViewById(R.id.lblPaymentCost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makePayments(ListView paymentListView, int[] paymentsAmount, int[] paymentsCost) {
        try {
            ArrayList<Payment> paymentArrayList = genPaymentList(paymentsAmount.length, paymentsAmount, paymentsCost);
            PaymentListAdapter paymentListAdapter = new PaymentListAdapter(PaymentActivity.this, R.layout.payment_view_layout, paymentArrayList);
            paymentListView.setAdapter(paymentListAdapter);
            paymentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        JsonBuyCoin jsonBuyCoin = (JsonBuyCoin) new JsonBuyCoin(paymentsCost[position], PaymentActivity.this).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Payment> genPaymentList(int paymentsCount, int[] paymentsAmount, int[] paymentsCost) {
        ArrayList<Payment> paymentArrayList = new ArrayList<>();
        try {
            Payment[] payments = new Payment[paymentsCount];
            for (int i = 0 ; i < paymentsCount ; i++) {
                payments[i] = new Payment(paymentsAmount[i], paymentsCost[i]);
            }
            for (int j = 0 ; j < paymentsCount ; j++) {
                paymentArrayList.add(payments[j]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paymentArrayList;
    }

    public void updateBalance() {
        try {
            JsonGetBalance jsonGetBalance = (JsonGetBalance) new JsonGetBalance(UtilToken.token).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}