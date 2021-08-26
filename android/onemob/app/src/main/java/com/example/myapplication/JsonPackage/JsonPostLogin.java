package com.example.myapplication.JsonPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.UtilBalance;
import com.example.myapplication.UtilToConfirmFromLogin;
import com.example.myapplication.UtilToken;
import com.example.myapplication.VerificationActivity;
import com.example.myapplication.ui.notifications.NotificationsFragment;
import com.example.myapplication.ui.videos.VideosFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//This class for sending the username and password to the server to for logging in.
public class JsonPostLogin extends AsyncTask {
    public static final MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");

    String username = "";
    String password = "";
    boolean validEmail = false;
    String httpCode;

    String tokenLogin = "0";
    int balanceLogin = 0;

    Activity activity = null;
    ProgressDialog progressDialog;

    EditText editTextUserName;
    EditText editTextPassword;

    public JsonPostLogin(String username, String password, Activity activity, EditText editTextUserName, EditText editTextPassword, ProgressDialog progressDialog) {
        this.username = username;
        this.password = password;
        this.activity = activity;
        this.editTextUserName = editTextUserName;
        this.editTextPassword = editTextPassword;
        this.progressDialog = progressDialog;
    }

    public JsonPostLogin() { }

    public JsonPostLogin(Activity activity ,String username, String password) {
        this.activity = activity;
        this.username = username;
        this.password = password;
    }

    public String getToken() {
        return tokenLogin;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username",username);
                jsonObject.put("password",password);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JsonObject Error!", e.getMessage());
            }
            OkHttpClient okHttpClient = new OkHttpClient.Builder().
                    connectTimeout(15, TimeUnit.SECONDS).
                    writeTimeout(15, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    build();
            String adminInfo = Credentials.basic("alireza","<!--comment>");
            RequestBody requestBody = RequestBody.create(jsonMediaType, jsonObject.toString());
            Request request = new Request.Builder().url("http://138.201.6.240:8000/api/login/").post(requestBody).addHeader("Authorization",adminInfo).build();
            Response response = null;
            String result = "";
            JSONObject jsonObjectToken = null;
            try {
                response = okHttpClient.newCall(request).execute();
                httpCode = String.valueOf(response.code());
                Log.d("httpCode", httpCode);
                result = response.body().string();
                jsonObjectToken = new JSONObject(result);
                if (httpCode.equals("200")){
                    tokenLogin = jsonObjectToken.getString("token");
                    validEmail = jsonObjectToken.getBoolean("email_valid");
                    balanceLogin = jsonObjectToken.getInt("balance");
                }
            } catch (IOException e) {
                e.printStackTrace();
                tokenLogin = "0";
                Log.e("error",e.getMessage());
                Toasty.error(activity, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
            } catch (NullPointerException e){
                e.printStackTrace();
                tokenLogin = "0";
                Log.e("error",e.getMessage());
                Toasty.error(activity, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
            }
            Log.d("Login Json!", result);
            Log.d("Token JsonPostLogin!", tokenLogin);
            Log.d("BalanceLogin", String.valueOf(balanceLogin));
        } catch (Exception e){
            e.printStackTrace();
            Log.e("Whole Exception!", e.getMessage());
            Toasty.error(activity, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            if (validEmail){
                if (httpCode.equals("200")){
                    UtilToken.token = tokenLogin;
                    UtilBalance.balance = balanceLogin;
                    Intent intentToMain = new Intent(activity, MainActivity.class);
                    activity.startActivity(intentToMain);
                    Toasty.success(activity, "ورود کاربر موفقیت آمیز بود.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else if (httpCode.equals("400") || httpCode.equals("404")){
                    editTextUserName.setText("");
                    editTextPassword.setText("");
                    editTextUserName.setError("لطفا نام کاربری معتبر وارد کنید!");
                    editTextPassword.setError("لطفا رمز عبور معتبر وارد کنید!");
                    Toasty.error(activity, "نام کاربری یا رمز عبور معتبر نمی باشد!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            } else {
                if (httpCode.equals("404")){
                    Toasty.error(activity, "کاربر پیدا نشد!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else if (httpCode.equals("400")){
                    Toasty.error(activity, "رمز عبور اشتباه است!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else {
                    Toasty.error(activity, "لطفا ایمیل خود را تایید کنید!", Toast.LENGTH_LONG).show();
                    Intent intentToVerification = new Intent(activity, VerificationActivity.class);
                    UtilToConfirmFromLogin.isFromLogin = true;
                    activity.startActivity(intentToVerification);
                    progressDialog.dismiss();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Toasty.error(activity, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

    }
}
