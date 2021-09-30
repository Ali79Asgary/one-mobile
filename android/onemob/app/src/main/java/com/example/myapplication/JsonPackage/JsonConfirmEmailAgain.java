package com.example.myapplication.JsonPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JsonConfirmEmailAgain extends AsyncTask {

    String token;
    int responseCode = 0;
    ProgressDialog progressDialog;
    Context context;

    public JsonConfirmEmailAgain(String token) {
        this.token = token;
    }

    public JsonConfirmEmailAgain(String token, ProgressDialog progressDialog, Context context) {
        this.token = token;
        this.progressDialog = progressDialog;
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().
                    connectTimeout(15, TimeUnit.SECONDS).
                    writeTimeout(15, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("http://138.201.6.240:8000/api/resend-email/")
                    .method("POST", body)
                    .addHeader("Authorization", "Token "+token)
                    .addHeader("Cookie", "experimentation_subject_id=eyJfcmFpbHMiOnsibWVzc2FnZSI6IklqZzVaalk1T1RBeExUTmtZekV0TkRjMU5pMWlOR0U0TFRneVlqRXlOVFpsTVRGaU5TST0iLCJleHAiOm51bGwsInB1ciI6ImNvb2tpZS5leHBlcmltZW50YXRpb25fc3ViamVjdF9pZCJ9fQ%3D%3D--8612d03c3621a3786624d23905e2e20eda4a3d8e")
                    .build();        Response response = null;
            String result = "";
            JSONObject jsonObject = null;
            try {
                response = okHttpClient.newCall(request).execute();
                responseCode = response.code();
                result = response.body().string();
                jsonObject = new JSONObject(result);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            Log.d("Result Again!", result);
            Log.d("Response Again", String.valueOf(response));
            Log.d("JsonObject Again!", String.valueOf(jsonObject));
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            if (responseCode == 200) {
                Toasty.success(context, "کد تایید ایمیل دوباره ارسال شد!", Toast.LENGTH_LONG).show();
            } else {
                Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }
}
