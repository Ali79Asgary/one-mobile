package com.example.myapplication.JsonPackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.myapplication.UtilToken;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JsonBuyCoin extends AsyncTask {

    public static final MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
    int amount = 0;
    int responseCode = 0;
    String link = "";
    Context context;

    public JsonBuyCoin(int amount) {
        this.amount = amount;
    }

    public JsonBuyCoin(int amount, Context context) {
        this.amount = amount;
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            JSONObject amountObject = new JSONObject();
            try {
                amountObject.put("amount", amount);
            } catch (Exception e) {
                e.printStackTrace();
            }
            OkHttpClient okHttpClient = new OkHttpClient.Builder().
                    connectTimeout(15, TimeUnit.SECONDS).
                    writeTimeout(15, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    build();
            RequestBody requestBody = RequestBody.create(jsonMediaType, String.valueOf(amountObject));
            Request request = new Request.Builder().url("http://138.201.6.240:8000/api/payment/create/").method("POST", requestBody).addHeader("Authorization", "Token "+UtilToken.token).build();
            Response response = null;
            JSONObject resultJSONObject = null;
            String buyCoinResult = "";
            try {
                response = okHttpClient.newCall(request).execute();
                responseCode = response.code();
                System.out.println(response);
                buyCoinResult = response.body().string();
                System.out.println(buyCoinResult);
                resultJSONObject = new JSONObject(buyCoinResult);
                link = resultJSONObject.getString("link");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            context.startActivity(browserIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPostExecute(o);
    }
}
