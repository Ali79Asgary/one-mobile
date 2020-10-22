package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.JsonPackage.JsonPostRegistration;

/**
 * Created by tutlane on 08-01-2018.
 */

public class RegistrationActivity extends AppCompatActivity {

    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextUserName;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextConfirmPassword;
    Button btnRegistration;
    TextView lblRegistrationToLogin;
    TextView lblRegistrationStatus;

    JsonPostRegistration jsonPostRegistration = null;

    String firstName = "";
    String lastName = "";
    String userName = "";
    String email = "";
    String password = "";
    String confirmPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        try {
            setupUI();
            setupListeners();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setupUI(){
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnRegistration = findViewById(R.id.btnRegistration);
        lblRegistrationToLogin = findViewById(R.id.lblRegistrationToLogin);
        lblRegistrationStatus = findViewById(R.id.lblRegistrationStatus);
    }

    private void setupListeners(){
        lblRegistrationToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registrationIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(registrationIntent);
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = editTextFirstName.getText().toString();
                lastName = editTextLastName.getText().toString();
                userName = editTextUserName.getText().toString();
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                confirmPassword = editTextConfirmPassword.getText().toString();
                if (checkFieldsEmpty(firstName, lastName, userName, email, password, confirmPassword) == 0){
                    int checkPassword = 0;
                    if (password.equals(confirmPassword)){
                        checkPassword = 1;
                    }
                    if (checkPassword == 1){
                        jsonPostRegistration = (JsonPostRegistration) new  JsonPostRegistration(RegistrationActivity.this, editTextFirstName,editTextLastName,editTextUserName,editTextEmail,editTextPassword,editTextConfirmPassword,lblRegistrationStatus,userName,firstName,lastName,email,password).execute();
                    } else {
                        editTextConfirmPassword.setError("لطفا رمز عبور را درست وارد کنید!");
                    }
                }
            }
        });
    }

    private int checkFieldsEmpty(String firstName, String lastName, String userName, String email, String password, String confirmPassword){
        int checkFieldsEmpty = 0;
        if (firstName.equals("")){
            editTextFirstName.setError("لطفا نام خود را وارد کنید!");
            checkFieldsEmpty++;
        }
        if (lastName.equals("")){
            editTextLastName.setError("لطفا نام خانوادگی خود را وارد کنید!");
            checkFieldsEmpty++;
        }
        if (userName.equals("")){
            editTextUserName.setError("لطفا نام کاربری خود را وارد کنید!");
            checkFieldsEmpty++;
        }
        if (email.equals("")){
            editTextEmail.setError("لطفا ایمیل خود را وارد کنید!");
            checkFieldsEmpty++;
        }
        if (password.equals("")){
            editTextPassword.setError("لطفا رمز عبور خود را وارد کنید!");
            checkFieldsEmpty++;
        }
        if (confirmPassword.equals("")){
            editTextConfirmPassword.setError("لطفا رمز عبور خود را تائید کنید!");
            checkFieldsEmpty++;
        }
        return checkFieldsEmpty;
    }
}