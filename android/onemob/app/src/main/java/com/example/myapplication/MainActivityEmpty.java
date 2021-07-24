package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivityEmpty extends AppCompatActivity{

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent activityIntent;

//    int temp = 1;
    int temp = 2;
//    int temp = 3;

//        String token = null;


    if (temp == 1) {
      activityIntent = new Intent(this, MainActivity.class);
      Log.d("", "-----------------------------------Main Activity launched ----------------------------------------------------------------------------------------------------");
    } else if(temp == 2) {
      activityIntent = new Intent(this, LoginActivity.class);
      Log.d("", "-----------------------------------Login Activity launched --------------------------------------------------------------------------------------------------");
    }
    else {
      activityIntent = new Intent(this, RegistrationActivity.class);
      Log.d("", "-----------------------------------Registration Activity launched --------------------------------------------------------------------------------------------------");
    }
//        if (Util.getToken() != null) {
//            activityIntent = new Intent(this, MainActivity.class);
//        } else {
//            activityIntent = new Intent(this, LoginActivity.class);
//        }
    startActivity(activityIntent);
    finish();
  }


}