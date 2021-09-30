package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.JsonPackage.JsonConfirmEmail;
import com.example.myapplication.JsonPackage.JsonConfirmEmailAgain;

import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class VerificationActivity extends AppCompatActivity {

EditText editTextConfirmEmailNotification;
Button btnConfirmEmailNotification;
TextView lblConfirmStatus;
TextView lblVerificationTimer;
ProgressDialog progressDialog;

String confirmEmailCode;
String token;

CountDownTimer countDownTimer;

long timeLeftInMillsSecond = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        try {
            setUI();
            setListener();
            startTimer();
        } catch (Exception e){
            e.printStackTrace();
            Toasty.error(getApplicationContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }

    private void setUI(){
        editTextConfirmEmailNotification = findViewById(R.id.editTextConfirmEmailNotification);
        btnConfirmEmailNotification = findViewById(R.id.btnConfirmEmailNotification);
        lblVerificationTimer = findViewById(R.id.lblVerificationTimer);
    }

    private void setListener(){
        try {
            btnConfirmEmailNotification.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    try {
                        confirmEmailCode = editTextConfirmEmailNotification.getText().toString();
                        try {
                            token = UtilToken.token;
                        } catch (NullPointerException e){
                            e.printStackTrace();
                            token = "The token was null.";
                        }
                        progressDialog = new ProgressDialog(v.getContext());
                        progressDialog.setMessage("Loading...");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.create();
                        progressDialog.show();
                        JsonConfirmEmail jsonConfirmEmail = (JsonConfirmEmail) new JsonConfirmEmail(
                                token,
                                confirmEmailCode,
                                editTextConfirmEmailNotification,
                                lblConfirmStatus,
                                VerificationActivity.this,
                                progressDialog).
                                execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toasty.error(getApplicationContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(getApplicationContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }

    public static String timeManager(long milliSecond){
        int minute = 0;
        String resMinute = "";
        int second = 0;
        String resSecond = "";
        String result = "";
        minute = (int) (milliSecond/60000);
        second = (int) ((milliSecond%60000)/1000);
        if (minute<10){
            resMinute ="0"+String.valueOf(minute);
        } else {
            resMinute = String.valueOf(minute);
        }
        if (second<10){
            resSecond = "0"+String.valueOf(second);
        } else {
            resSecond = String.valueOf(second);
        }
        return resMinute+":"+resSecond;
    }

    public void startTimer(){
        try {
            if (UtilToConfirmFromLogin.isFromLogin){
                timeLeftInMillsSecond = 0;
            } else {
                timeLeftInMillsSecond = 60000;
            }
            UtilToConfirmFromLogin.isFromLogin = false;
            countDownTimer = new CountDownTimer(timeLeftInMillsSecond, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillsSecond = millisUntilFinished;
                    updateTimer();
                }

                @Override
                public void onFinish() {
                    try {
                        Log.d("Timer Finish", "Finished");
                        lblVerificationTimer.setText("ارسال مجدد");
                        lblVerificationTimer.setTextColor(Color.WHITE);
                        lblVerificationTimer.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {
                                try {
                                    progressDialog = new ProgressDialog(v.getContext());
                                    progressDialog.setMessage("Loading...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.create();
                                    progressDialog.show();
                                    JsonConfirmEmailAgain jsonConfirmEmailAgain = (JsonConfirmEmailAgain) new JsonConfirmEmailAgain(UtilToken.token).execute();
                                    Log.d("Timer Resend", "Resend Email");
                                    timeLeftInMillsSecond = 60000;
                                    if (UtilResendEmail.stillResendingEmail){
                                        startTimer();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toasty.error(getApplicationContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toasty.error(getApplicationContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(getApplicationContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }

    public void updateTimer(){
        try {
            lblVerificationTimer.setText(timeManager(timeLeftInMillsSecond));
            if (lblVerificationTimer.getText().toString().equals("00:00")){
                JsonConfirmEmailAgain jsonConfirmEmailAgain = new JsonConfirmEmailAgain(UtilToken.token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(getApplicationContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }
}