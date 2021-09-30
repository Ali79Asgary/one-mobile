package com.example.myapplication.JsonPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.UtilToken;
import com.example.myapplication.VerificationActivity;

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

public class JsonPostRegistration extends AsyncTask {
    public static final MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");


    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextUserName;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextConfirmPassword;
    TextView lblRegistrationStatus;
    Context context;
    ProgressDialog progressDialog;

    String username = "";
    String first_name = "";
    String last_name = "";
    String email = "";
    String password = "";
    String httpCode = "";
    String token = "";

    public JsonPostRegistration(
            Context context,
            EditText editTextFirstName,
            EditText editTextLastName,
            EditText editTextUserName,
            EditText editTextEmail,
            EditText editTextPassword,
            EditText editTextConfirmPassword,
            TextView lblRegistrationStatus,
            String username,
            String first_name,
            String last_name,
            String email,
            String password,
            ProgressDialog progressDialog) {
        this.context = context;
        this.editTextFirstName = editTextFirstName;
        this.editTextLastName = editTextLastName;
        this.editTextUserName = editTextUserName;
        this.editTextEmail = editTextEmail;
        this.editTextPassword = editTextPassword;
        this.editTextConfirmPassword = editTextConfirmPassword;
        this.lblRegistrationStatus = lblRegistrationStatus;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.progressDialog = progressDialog;
    }

    public JsonPostRegistration(String username, String first_name, String last_name, String email, String password) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", username);
                jsonObject.put("first_name", first_name);
                jsonObject.put("last_name", last_name);
                jsonObject.put("email", email);
                jsonObject.put("password", password);
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
            Request request = new Request.Builder().url("http://138.201.6.240:8000/api/create/").post(requestBody).addHeader("Authorization", adminInfo).build();
            Response response = null;
            JSONObject jsonObjectResult = null;
            String result = "";
            int code = 0;
            try {
                response = okHttpClient.newCall(request).execute();
                Log.d("ResponseRegistration", String.valueOf(response));
                result = response.body().string();
                Log.d("ResultPostRegistration!", result);
                jsonObjectResult = new JSONObject(result);
                httpCode = String.valueOf(response.code());
                if (httpCode.equals("200")){
                    token = jsonObjectResult.getString("token");
                } else if (httpCode.equals("400")){
                    try {
                        username = jsonObjectResult.getString("username");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        email = jsonObjectResult.getString("email");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                code = response.code();
            } catch (IOException e) {
                e.printStackTrace();
                token = "0";
                Log.e("Error",e.getMessage());
            }
            Log.d("token",token);
            Log.d("code", String.valueOf(code));
            Log.d("Result Registration!", result);
            Log.d("username Registration!", username);
            Log.d("email Registration!", email);
        } catch (Exception e){
            e.printStackTrace();
            Log.e("Whole Exception!", e.getMessage());
            progressDialog.dismiss();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            if (httpCode.equals("200")){
                Toasty.success(context,"ثبت نام با موفقیت انجام شد!", Toast.LENGTH_LONG).show();
                UtilToken.token = token;
                Log.d("UtilToken", token);
                Intent intentToVerification = new Intent(context, VerificationActivity.class);
                context.startActivity(intentToVerification);
                progressDialog.dismiss();
            } else if (httpCode.equals("400")){
                if (username.equals("[\"A user with that username already exists.\"]") && email.equals("[\"Enter a valid email address.\"]")){
                    Toasty.error(context, "نام کاربری از قبل وجود دارد و ایمیل وارد شده معتبر نمی باشد!", Toast.LENGTH_LONG).show();
                } else if (username.equals("[\"A user with that username already exists.\"]") && email.equals("[\"Student with this email address already exists.\"]")){
                    Toasty.error(context, "نام کاربری و ایمیل از قبل وجود دارند!", Toast.LENGTH_LONG).show();
                } else if (email.equals("[\"Student with this email address already exists.\"]")){
                    Toasty.error(context, "ایمیل از قبل وجود دارد!", Toast.LENGTH_LONG).show();
                } else if (email.equals("[\"Enter a valid email address.\"]")){
                    Toasty.error(context, "ایمیل وارد شده معتبر نمی باشد!", Toast.LENGTH_LONG).show();
                } else if (username.equals("[\"A user with that username already exists.\"]")){
                    Toasty.error(context, "نام کاربری از قبل وجود دارد!", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            } else {
                Toasty.error(context, "ثبت نام موفقیت آمیز نبود!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        } catch (Exception e){
            e.printStackTrace();
            Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }
}
