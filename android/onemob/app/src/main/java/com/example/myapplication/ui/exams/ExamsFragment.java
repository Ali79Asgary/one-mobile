package com.example.myapplication.ui.exams;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
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
import com.example.myapplication.UtilProgressDialog;
import com.example.myapplication.UtilToken;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ExamsFragment extends Fragment {

    private ExamsViewModel examsViewModel;
    TextView lblShowBalance;
    ListView examsListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        examsViewModel = ViewModelProviders.of(this).get(ExamsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_exams, container, false);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
        try {
            UtilProgressDialog.progressDialog = new ProgressDialog(getContext());
            UtilProgressDialog.progressDialog.setMessage("Loading...");
            UtilProgressDialog.progressDialog.setCancelable(false);
            UtilProgressDialog.progressDialog.setCanceledOnTouchOutside(false);
            UtilProgressDialog.progressDialog.create();
            UtilProgressDialog.progressDialog.show();
            setUI(root);
            updateBalance();
            JsonGetQuizzes jsonGetQuizzes = (JsonGetQuizzes) new JsonGetQuizzes(
                    UtilToken.token,
                    getActivity(),
                    getContext(),
                    examsListView,
                    lblShowBalance).
                    execute();

        } catch (Exception e){
            e.printStackTrace();
            Toasty.error(getContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
        return root;
    }

    private void setUI(View root) {
        try {
            examsListView = root.findViewById(R.id.examsListView);
            lblShowBalance = root.findViewById(R.id.lblBalanceExam);
            lblShowBalance.setText(String.valueOf(UtilBalance.balance));
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(getContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }

    public void updateBalance() {
        try {
            JsonGetBalance jsonGetBalance = (JsonGetBalance) new JsonGetBalance(UtilToken.token).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(getContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }
}
