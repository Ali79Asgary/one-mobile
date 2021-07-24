package com.example.myapplication.JsonPackage;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.myapplication.UtilBalance;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;

public class JsonGetBalance extends AsyncTask {

    String token;
    int balance = 0;

    public JsonGetBalance(String token) { this.token = token; }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url("http://138.201.6.240:8000/api/get-balance").method("GET", null).addHeader("Authorization", "Token "+token).build();
            Response response = null;
            JSONObject jsonObject = null;
            String balanceResultJson = "";
            try {
                response = okHttpClient.newCall(request).execute();
                balanceResultJson = response.body().string();
                Log.d("balanceJsonResult", balanceResultJson);
                jsonObject = new JSONObject(balanceResultJson);
                balance = jsonObject.getInt("balance");
            } catch (IOException | JSONException e){
                e.printStackTrace();
            }
            Log.d("balanceJson", String.valueOf(balance));
            UtilBalance.balance = balance;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            UtilBalance.balance = balance;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
