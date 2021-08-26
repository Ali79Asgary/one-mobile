package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.JsonPackage.JsonPostLogin;
import com.example.myapplication.ui.notifications.NotificationsFragment;
import com.example.myapplication.ui.videos.VideosFragment;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUserName;
    EditText editTextPassword;
    Button btnLogin;
    TextView lblLoginToRegister;
    ProgressDialog progressDialog;

    JsonPostLogin jsonPostLogin = null;

    String userName = "";
    String password = "";
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.login_activity);
            setupUI();
            setupListeners();
        } catch (Exception e){
            e.printStackTrace();
            Log.e("Whole Exception!", e.getMessage());
            Toasty.error(this, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }

    private void setupUI(){
        editTextUserName = findViewById(R.id.editTextUserNameLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        lblLoginToRegister = findViewById(R.id.lblLoginToRegistration);
    }

    private void setupListeners(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (checkUserPassEmpty() == 2){
                    checkUserPassTruth();
                } else {
                    Toasty.error(getApplicationContext(), "لطفا تمامی فیلدها را تکمیل نمایید!", Toast.LENGTH_LONG).show();
                }
            }
        });

        lblLoginToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    //This method checks username and password fields to being empty.
    private int checkUserPassEmpty(){
        userName = editTextUserName.getText().toString();
        password = editTextPassword.getText().toString();
        int checkEmpty = 0;
        if (userName.equals("")){
            editTextUserName.setError("لطفا نام کاربری خود را وارد کنید!");
        } else {
            checkEmpty++;
        }
        if (password.equals("")){
            editTextPassword.setError("لطفا رمز عبور خود را وارد کنید!");
        } else {
            checkEmpty++;
        }

        return checkEmpty;
    }

    //This method checks username and password are truth or not.
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkUserPassTruth(){
        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.create();
            progressDialog.show();
            jsonPostLogin = (JsonPostLogin) new JsonPostLogin(userName,password,LoginActivity.this,editTextUserName,editTextPassword, progressDialog).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(this, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }

}
