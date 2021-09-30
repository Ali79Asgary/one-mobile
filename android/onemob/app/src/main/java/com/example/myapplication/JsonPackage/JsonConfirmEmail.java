package com.example.myapplication.JsonPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.UtilResendEmail;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

//This class for confirming user email with a code which is sent to the user email.
public class JsonConfirmEmail extends AsyncTask {

    public static final MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");

    String confirmEmailCode = "";
    String tokenConfirmEmail = "";
    String confirmEmailStatus = "";

    EditText editTextConfirmEmailNotification;
    TextView lblConfirmStatus;
    Context context;
    ProgressDialog progressDialog;

    boolean stillResendingEmail;

    public JsonConfirmEmail(String token, String confirmEmailCode, EditText editTextConfirmEmailNotification, TextView lblConfirmStatus, Context context) {
        this.tokenConfirmEmail = token;
        this.confirmEmailCode = confirmEmailCode;
        this.editTextConfirmEmailNotification = editTextConfirmEmailNotification;
        this.lblConfirmStatus = lblConfirmStatus;
        this.context = context;
    }

    public JsonConfirmEmail(
            String confirmEmailCode,
            String tokenConfirmEmail,
            EditText editTextConfirmEmailNotification,
            TextView lblConfirmStatus,
            Context context,
            ProgressDialog progressDialog) {
        this.confirmEmailCode = confirmEmailCode;
        this.tokenConfirmEmail = tokenConfirmEmail;
        this.editTextConfirmEmailNotification = editTextConfirmEmailNotification;
        this.lblConfirmStatus = lblConfirmStatus;
        this.context = context;
        this.progressDialog = progressDialog;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            JSONObject confirmEmailJson = new JSONObject();
            try {
                confirmEmailJson.put("token", tokenConfirmEmail);
                confirmEmailJson.put("verification_key", confirmEmailCode);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JsonObject Error!", e.getMessage());
            }
            OkHttpClient okHttpClient = new OkHttpClient.Builder().
                    connectTimeout(15, TimeUnit.SECONDS).
                    writeTimeout(15, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    build();
            RequestBody requestBody = RequestBody.create(jsonMediaType,confirmEmailJson.toString());
            Request request = new Request.Builder().url("http://138.201.6.240:8000/api/verify-email/").post(requestBody).addHeader("Authorization", "Token "+tokenConfirmEmail).build();
            Response response = null;
            String result = "";
            JSONObject jsonObjectResponse = null;
            try {
                response = okHttpClient.newCall(request).execute();
                result = response.body().string();
                jsonObjectResponse = new JSONObject(result);
                confirmEmailStatus = jsonObjectResponse.getString("response");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                tokenConfirmEmail = "0";
                confirmEmailStatus = "0";
                Log.e("Response IOException!", e.getMessage());
            }
            Log.d("Response Result!", result);
            Log.d("ConfirmEmailStatus", confirmEmailStatus);
            Log.d("Token ConfirmEmail!", tokenConfirmEmail);
        } catch (Exception e){
            e.printStackTrace();
            confirmEmailStatus = "0";
            Log.e("Whole Exception!", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            Log.d("ConfirmEmailStatus", confirmEmailStatus);
            if (confirmEmailStatus.equals("successfully verified the email")){
                Toasty.success(context, "تایید ایمیل موفقیت آمیز بود!", Toast.LENGTH_LONG).show();
                UtilResendEmail.stillResendingEmail = false;
                progressDialog.dismiss();
                Intent intentToMain = new Intent(context, MainActivity.class);
                context.startActivity(intentToMain);
            } else {
                Toasty.error(context, "تائید ایمیل ناموفق بود!", Toast.LENGTH_LONG).show();
                editTextConfirmEmailNotification.setText("");
                progressDialog.dismiss();
            }
        } catch (Exception e){
            e.printStackTrace();
            Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }
}
