package com.example.myapplication.JsonPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myapplication.UtilBalance;
import com.example.myapplication.UtilProgressDialog;
import com.example.myapplication.UtilToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JsonBuyVideo extends AsyncTask {

    public static final MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");

    String token = "";
    String videoTitle = "";
    int buyVideoBalance = 0;
    int buyVideoStatusCode = 0;
    int responseCode = 0;
    Activity activity;
    Context context;
    ListView videoListView;
    ImageView lockVideoImageView;
    TextView lblShowBalance;
    ProgressDialog progressDialog;

    public JsonBuyVideo(
            String token,
            String videoTitle,
            Activity activity,
            Context context,
            ListView videoListView,
            ImageView lockVideoImageView,
            TextView lblShowBalance,
            ProgressDialog progressDialog) {
        this.token = token;
        this.videoTitle = videoTitle;
        this.activity = activity;
        this.context = context;
        this.videoListView = videoListView;
        this.lockVideoImageView = lockVideoImageView;
        this.lblShowBalance = lblShowBalance;
        this.progressDialog = progressDialog;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().
                    connectTimeout(15, TimeUnit.SECONDS).
                    writeTimeout(15, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    build();
            RequestBody requestBody = RequestBody.create(jsonMediaType, token);
            Request request = new Request.Builder().url("http://138.201.6.240:8000/api/buy-video/"+videoTitle+"/").method("POST", requestBody).addHeader("Authorization", "Token "+token).build();
            Response response = null;
            JSONObject jsonObject = null;
            String buyVideoResult = "";
            try {
                response = okHttpClient.newCall(request).execute();
                responseCode = response.code();
                buyVideoResult = response.body().string();
                jsonObject = new JSONObject(buyVideoResult);
                buyVideoBalance = jsonObject.getInt("balance");
                buyVideoStatusCode = response.code();
            } catch (IOException e){
                e.printStackTrace();
            }
            Log.d("buyVideoResult", buyVideoResult);
            Log.d("buyVideoStatusCode", String.valueOf(buyVideoStatusCode));
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
//            progressDialog = new ProgressDialog(context);
//            showProgressDialog(progressDialog);
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            if (responseCode == 200){
                updateBalance();
                Toasty.success(activity, "خرید ویدیو با موفقیت انجام شد.", Toast.LENGTH_LONG).show();
                try {
                    lblShowBalance.setText(String.valueOf(UtilBalance.balance));
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                JsonGetBalance jsonGetBalance = (JsonGetBalance) new JsonGetBalance(UtilToken.token).execute();
                JsonVideoList jsonVideoList = (JsonVideoList) new JsonVideoList(
                        UtilToken.token,
                        activity,
                        context,
                        videoListView,
                        lockVideoImageView,
                        lblShowBalance,
                        progressDialog).
                        execute();
                UtilProgressDialog.progressDialog.dismiss();
            } else {
                UtilProgressDialog.progressDialog.dismiss();
                Toasty.error(activity, "خرید ویدیو ناموفق بود.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
            UtilProgressDialog.progressDialog.dismiss();
            Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showProgressDialog(ProgressDialog progressDialog) {
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.create();
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBalance() {
        try {
            JsonGetBalance jsonGetBalance = (JsonGetBalance) new JsonGetBalance(UtilToken.token).execute();
            lblShowBalance.setText(String.valueOf(UtilBalance.balance));
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }
}
