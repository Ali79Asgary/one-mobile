package com.example.myapplication.JsonPackage;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//This class is create new thread to run the server section and read the json from server.
public class JsonGet extends AsyncTask {
    JsonFields jsonFields;
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            OkHttpClient client = new OkHttpClient.Builder().
                    connectTimeout(15, TimeUnit.SECONDS).
                    writeTimeout(15, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    build();
            Request request = new Request.Builder().url("http://192.168.1.5:8000/api").method("GET",null).build();
            Response response = null;
            String string = "hello";
            JSONArray jsonArray = null;
            JSONObject jsonObject = new JSONObject();
            try {
                response = client.newCall(request).execute();
                string = response.body().string();
                jsonArray = new JSONArray(string);
                jsonObject = jsonArray.getJSONObject(0);
                jsonFields = new JsonFields(jsonObject.getInt("id"),jsonObject.getString("username"),jsonObject.getString("first_name"),jsonObject.getString("last_name"),jsonObject.getString("email"),jsonObject.getString("password"),jsonObject.getString("last_login"),jsonObject.getString("date_joined"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Hello",string);
            Log.d("JsonField",jsonFields.toString());
        } catch (Exception e){

        }
        return null;
    }
}
