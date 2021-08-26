package com.example.myapplication.JsonPackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class JsonVideosDownload extends AsyncTask {

    String token;
    Context context = null;
    int httpCode = 0;
    String videoTitle = "";
    String fileName = "Alineo";
    Response responseVideoDownload = null;
    File fileVideoDownload = null;
    File root;
    FileOutputStream fileOutputStream;
    String pathVideoDownload;

    public JsonVideosDownload(String token, String videoTitle) {
        this.token = token;
        this.videoTitle = videoTitle;
    }

    public JsonVideosDownload(String token) {
        this.token = token;
    }

    public JsonVideosDownload(String token, Context context) {
        this.token = token;
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            OkHttpClient clientVideoDownload = new OkHttpClient.Builder().
                    connectTimeout(15, TimeUnit.SECONDS).
                    writeTimeout(15, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    build();
            Request requestVideoDownload = new Request.Builder().url("http://192.168.1.5:8000/api/video/"+videoTitle).method("GET", null).addHeader("Authorization", "Token "+token).build();
            responseVideoDownload = null;
            try {
//                pathVideoDownload = "/storage/emulated/0/OneMob";
                pathVideoDownload = context.getExternalFilesDir(null).getAbsolutePath();
                fileVideoDownload = new File(pathVideoDownload);
                if (!fileVideoDownload.exists()){
                    fileVideoDownload.mkdirs();
                    Log.d("FILE EXISTS", fileVideoDownload.getAbsolutePath());
                }
                root = new File(fileVideoDownload, fileName);
                responseVideoDownload = clientVideoDownload.newCall(requestVideoDownload).execute();
                httpCode = responseVideoDownload.code();
                fileOutputStream = new FileOutputStream(root);
                fileOutputStream.write(responseVideoDownload.body().bytes());
                fileOutputStream.close();
                responseVideoDownload.body().close();
                File newName = new File("Alineo");
                File[] listOne = fileVideoDownload.listFiles();
                for (int i = 0 ; i < listOne.length ; i++){
                    Log.d("listOne55555555555555", listOne[i].getAbsolutePath());
                    Log.d("listOneName", listOne[i].getName());
                    listOne[i].renameTo(newName);
                    Log.d("listOneNameNew", listOne[i].getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            if (httpCode == 401){
                Toast.makeText(context, "کاربر شناخته شده نیست!", Toast.LENGTH_LONG).show();
                Intent intentToLogin = new Intent(context, LoginActivity.class);
                context.startActivity(intentToLogin);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getFileCount(){
        int fileCount = 0;
//        File dir = new File("/storage/emulated/0/OneMob");
        File dir = new File(context.getExternalFilesDir(null).getAbsolutePath());
        File[] files = dir.listFiles();
        fileCount = files.length;
        return fileCount;
    }

    public String[] getFileName(){
//        File dir = new File("/storage/emulated/0/OneMob");
        File dir = new File(context.getExternalFilesDir(null).getAbsolutePath());
        File[] files = dir.listFiles();
        String[] filesName = new String[files.length];
        for (int i = 0 ; i < files.length ; i++){
            filesName[i] = files[i].getName();
        }
        return filesName;
    }

    private ArrayList<Video> genVideos(int fileCount, String[] videosName) {
        ArrayList<Video> videos = new ArrayList<Video>();
        Video[] arrayVideo = new Video[fileCount];
        for (int k = 0 ; k < arrayVideo.length ; k++){
            arrayVideo[k] = new Video("link", videosName[k]);
        }
        for (int i = 0 ; i < fileCount ; i++){
            videos.add(arrayVideo[i]);
        }
        return videos;
    }
}
