package com.example.myapplication.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.JsonPackage.JsonConfirmEmail;
import com.example.myapplication.JsonPackage.JsonGetBalance;
import com.example.myapplication.JsonPackage.JsonPostLogin;
import com.example.myapplication.MainActivity;
import com.example.myapplication.More;
import com.example.myapplication.MoreListAdapter;
import com.example.myapplication.PaymentActivity;
import com.example.myapplication.R;
import com.example.myapplication.UtilBalance;
import com.example.myapplication.UtilToken;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    ListView moreListView;
    String token = "";
    ImageView moreImageView;
    TextView showBalance;
    String[] moreNames;
    int[] moreImagesID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        try {
            updateBalance();
            moreListView = root.findViewById(R.id.moreListView);
            moreImageView = root.findViewById(R.id.moreImageView);
            System.out.println(String.valueOf(UtilBalance.balance));
            showBalance = root.findViewById(R.id.lblBalanceNotification);
            showBalance.setText(String.valueOf(UtilBalance.balance));
            moreNames = new String[1];
            moreNames[0] = "افزایش موجودی حساب";
            moreImagesID = new int[1];
            moreImagesID[0] = R.drawable.ic_payment_card;
            makePayment(moreListView,moreNames, moreImagesID);
        } catch (Exception e){
            e.printStackTrace();
            Log.e("Whole Exception!", e.getMessage());
        }
        return root;
    }

    public void updateBalance() {
        try {
            JsonGetBalance jsonGetBalance = (JsonGetBalance) new JsonGetBalance(UtilToken.token).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makePayment(ListView moreListView, String[] moreNames, int moreImagesID[]) {
        try {
            ArrayList<More> moreArrayList = genMoreList(moreNames.length, moreNames, moreImagesID);
            MoreListAdapter moreListAdapter = new MoreListAdapter(getActivity(), R.layout.more_view_layout, moreArrayList);
            moreListView.setAdapter(moreListAdapter);
            moreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        Intent goToPaymentsPage = new Intent(getContext(), PaymentActivity.class);
                        getContext().startActivity(goToPaymentsPage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<More> genMoreList(int moreCount, String[] moreNames, int[] moreImagesID) {
        ArrayList<More> moreArrayList = new ArrayList<>();
        try {
            More[] mores = new More[moreCount];
            for (int i = 0 ; i < mores.length ; i++) {
                mores[i] = new More(moreNames[i], moreImagesID[i]);
            }
            for (int j = 0 ; j < mores.length ; j++) {
                moreArrayList.add(mores[j]);
            }
            return moreArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return moreArrayList;
        }
    }
}