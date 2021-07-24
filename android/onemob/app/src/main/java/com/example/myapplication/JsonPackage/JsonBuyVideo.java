package com.example.myapplication.JsonPackage;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.UtilBalance;
import com.example.myapplication.UtilToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    Activity activity;
    Context context;
    ListView videoListView;
    ImageView lockVideoImageView;
    TextView lblShowBalance;

    public JsonBuyVideo(String token, String videoTitle) {
        this.token = token;
        this.videoTitle = videoTitle;
    }

    public JsonBuyVideo(String token, String videoTitle, Activity activity, Context context) {
        this.token = token;
        this.videoTitle = videoTitle;
        this.activity = activity;
        this.context = context;
    }

    public JsonBuyVideo(String token, String videoTitle, Activity activity, Context context, ListView videoListView, ImageView lockVideoImageView) {
        this.token = token;
        this.videoTitle = videoTitle;
        this.activity = activity;
        this.context = context;
        this.videoListView = videoListView;
        this.lockVideoImageView = lockVideoImageView;
    }

    public JsonBuyVideo(String token, String videoTitle, Activity activity, Context context, ListView videoListView, ImageView lockVideoImageView, TextView lblShowBalance) {
        this.token = token;
        this.videoTitle = videoTitle;
        this.activity = activity;
        this.context = context;
        this.videoListView = videoListView;
        this.lockVideoImageView = lockVideoImageView;
        this.lblShowBalance = lblShowBalance;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            RequestBody requestBody = RequestBody.create(jsonMediaType, token);
            Request request = new Request.Builder().url("http://138.201.6.240:8000/api/buy-video/"+videoTitle+"/").method("POST", requestBody).addHeader("Authorization", "Token "+token).build();
            Response response = null;
            JSONObject jsonObject = null;
            String buyVideoResult = "";
            try {
                response = okHttpClient.newCall(request).execute();
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

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            if (buyVideoStatusCode == 200){
                Toast.makeText(activity, "خرید این ویدیو با موفقیت انجام شد.", Toast.LENGTH_LONG).show();
                try {
                    lblShowBalance.setText(String.valueOf(UtilBalance.balance));
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                JsonGetBalance jsonGetBalance = (JsonGetBalance) new JsonGetBalance(UtilToken.token).execute();
                JsonVideoList jsonVideoList = (JsonVideoList) new JsonVideoList(UtilToken.token, activity, context, videoListView, lockVideoImageView, lblShowBalance).execute();
            } else {
                Toast.makeText(activity, "خرید ویدیو ناموفق بود.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
