package com.example.myapplication.ui.exams;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.Exam;
import com.example.myapplication.ExamListAdapter;
import com.example.myapplication.JsonPackage.JsonGetBalance;
import com.example.myapplication.JsonPackage.JsonGetQuizzes;
import com.example.myapplication.R;
import com.example.myapplication.ShowExamActivity;
import com.example.myapplication.UtilBalance;
import com.example.myapplication.UtilToken;

import java.util.ArrayList;

public class ExamsFragment extends Fragment {

    private ExamsViewModel examsViewModel;
    String token;
    TextView lblShowBalance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        examsViewModel = ViewModelProviders.of(this).get(ExamsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_exams, container, false);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},111);

        try {
            updateBalance();
            final Context contextToShowExam = getContext();
            final ListView examsListView = root.findViewById(R.id.examsListView);
            lblShowBalance = root.findViewById(R.id.lblBalanceExam);
            lblShowBalance.setText(String.valueOf(UtilBalance.balance));
            try {
                token = UtilToken.token;
                Log.d("tokenFromArg", token);
            } catch (NullPointerException e){
                e.printStackTrace();
            }

            JsonGetQuizzes jsonGetQuizzes = (JsonGetQuizzes) new JsonGetQuizzes(token, getActivity(), getContext(), examsListView).execute();

        } catch (Exception e){
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                } else {
                    Toast.makeText(getContext(), "Permission denied...!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void updateBalance() {
        try {
            JsonGetBalance jsonGetBalance = (JsonGetBalance) new JsonGetBalance(UtilToken.token).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
