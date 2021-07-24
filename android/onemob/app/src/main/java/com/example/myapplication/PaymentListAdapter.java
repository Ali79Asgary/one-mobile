package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PaymentListAdapter extends ArrayAdapter<Payment> {

    private Context mContext;
    int mResource;

    public PaymentListAdapter(@NonNull Context context, int resource, @NonNull List<Payment> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try {
            int paymentAmount = getItem(position).getPaymentAmount();
            int paymentCost = getItem(position).getPaymentCost();

            Payment payment = new Payment(paymentAmount, paymentCost);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            TextView lblPaymentAmount = convertView.findViewById(R.id.lblPaymentAmount);
            lblPaymentAmount.setText(String.valueOf(paymentAmount)+"سکه");
            TextView lblPaymentCost = convertView.findViewById(R.id.lblPaymentCost);
            lblPaymentCost.setText(String.valueOf(paymentCost)+"ریال");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
