package com.example.myapplication.JsonPackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ShowVideoActivity;
import com.example.myapplication.ShowVideoThirdActivity;
import com.example.myapplication.UtilContentLengths;
import com.example.myapplication.UtilToken;
import com.example.myapplication.Video;
import com.example.myapplication.VideoListAdapter;
import com.example.myapplication.ui.videos.VideosFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonVideoList extends AsyncTask {

    ArrayList<JSONObject> videosJsonObjectArrayList = new ArrayList<>();
    String[] videosName = null;
    String[] videosTitle = null;
    int[] videosPrice = null;
    boolean[] videosPurchased = null;
    String token;
    Activity activity = null;
    Context context = null;
    ListView videosListView;
    String path;
    File file;
    ImageView lockVideoImageView;
    TextView lblShowBalance;

    public JsonVideoList(String token, Activity activity, Context context, ListView videosListView, ImageView lockVideoImageView) {
        this.token = token;
        this.activity = activity;
        this.context = context;
        this.videosListView = videosListView;
        this.lockVideoImageView = lockVideoImageView;
    }

    public JsonVideoList(String token, Context context, Activity activity, ListView videosListView) {
        this.token = token;
        this.context = context;
        this.activity = activity;
        this.videosListView = videosListView;
    }

    public JsonVideoList(String token) {
        this.token = token;
    }

    public JsonVideoList(String token, Activity activity, ListView videosListView) {
        this.token = token;
        this.activity = activity;
        this.videosListView = videosListView;
    }

    public JsonVideoList(String token, Activity activity, Context context, ListView videosListView, ImageView lockVideoImageView, TextView lblShowBalance) {
        this.token = token;
        this.activity = activity;
        this.context = context;
        this.videosListView = videosListView;
        this.lockVideoImageView = lockVideoImageView;
        this.lblShowBalance = lblShowBalance;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            path = "/storage/emulated/0/OneMob";
            file = new File(path);
            if (!file.exists()){
                file.mkdirs();
                Log.d("FILE EXISTS", file.getAbsolutePath());
            }
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url("http://138.201.6.240:8000/api/videos").method("GET", null).addHeader("Authorization", "Token "+token).build();
            Response response = null;
            String resultVideoList = "";
            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            JSONObject video1 = null;
            String video1Name = "";
            int jsonArrayLength = 0;
            try {
                response = client.newCall(request).execute();
                resultVideoList = response.body().string();
                jsonObject = new JSONObject(resultVideoList);
                jsonArray = jsonObject.getJSONArray("videos");
                jsonArrayLength = jsonArray.length();

                videosName = new String[jsonArrayLength];
                for (int i = 0 ; i < jsonArrayLength ; i++){
                    videosJsonObjectArrayList.add(jsonArray.getJSONObject(i));
                }
                for (int j = 0 ; j < videosJsonObjectArrayList.size() ; j++){
                    videosName[j] = videosJsonObjectArrayList.get(j).getString("name");
                }

                videosTitle = new String[jsonArrayLength];
                for (int l = 0 ; l < jsonArrayLength ; l++){
                    videosTitle[l] = videosJsonObjectArrayList.get(l).getString("title");
                }

                videosPrice = new int[jsonArrayLength];
                for (int h = 0 ; h < jsonArrayLength ; h++){
                    videosPrice[h] = videosJsonObjectArrayList.get(h).getInt("price");
                }

                videosPurchased = new boolean[jsonArrayLength];
                for (int r = 0 ; r < jsonArrayLength ; r++){
                    videosPurchased[r] = videosJsonObjectArrayList.get(r).getBoolean("purchased");
                }
                video1 = jsonArray.getJSONObject(0);
                video1Name = video1.getString("name");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            Log.d("token", token);
            Log.d("resultVideoList", resultVideoList);
            Log.d("JsonObject", String.valueOf(jsonObject));
            Log.d("videosArray", String.valueOf(jsonArray));
            Log.d("video1", String.valueOf(video1));
            Log.d("video1Name", video1Name);
            Log.d("jsonArrayLength", String.valueOf(jsonArrayLength));
            for (int k = 0 ; k < videosName.length ; k++){
                Log.d("VideosName", videosName[k]);
                Log.d("VideosTitle", videosTitle[k]);
                Log.d("VideosPrice", String.valueOf(videosPrice[k]));
                Log.d("VideosPurchased", String.valueOf(videosPurchased[k]));
            }
//            for (int z = 0 ; z < videosTitle.length ; z++){
//                Log.d("VideosTitle", videosTitle[z]);
//            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            int fileCount = videosName.length;
            Log.d("fileCount", String.valueOf(fileCount));
            if (UtilContentLengths.contentLengths.size() < fileCount){
                for (int i = UtilContentLengths.contentLengths.size() ; i < fileCount ; i++){
                    UtilContentLengths.contentLengths.add(i, "");
                }
            }
//            for (int j = 0 ; j < videosName.length ; j++){
//                if (videosPurchased[j]){
//                    lockVideoImageView.setImageResource(R.drawable.ic_lock_open);
//                } else {
//                    lockVideoImageView.setImageResource(R.drawable.ic_password);
//                }
//            }
            ArrayList<Video> videosList = genVideos(fileCount, videosTitle, videosPurchased);
            final VideoListAdapter adapter = new VideoListAdapter(activity, R.layout.videos_view_layout, videosList);
            videosListView.setAdapter(adapter);
            videosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if (videosPurchased[position]){
                        JsonVideosDownload jsonVideosDownloadList = new JsonVideosDownload(token, videosTitle[position]);
                        Intent toShowVideo = new Intent(context, ShowVideoThirdActivity.class);
                        toShowVideo.putExtra("tokenShowVideo", token);
                        toShowVideo.putExtra("VideoTitleList", videosTitle[position]);
                        toShowVideo.putExtra("videosTitle", videosTitle);
                        toShowVideo.putExtra("videosName", videosName);
                        toShowVideo.putExtra("position", position);
                        context.startActivity(toShowVideo);
                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                        alertDialog.setTitle("فیلم                                                       ");
                        alertDialog.setMessage("این فیلم خریداری نشده است. آیا مایل به خرید هستید؟");
                        alertDialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JsonBuyVideo jsonBuyVideo = (JsonBuyVideo) new JsonBuyVideo(UtilToken.token, videosTitle[position], activity, context, videosListView, lockVideoImageView, lblShowBalance).execute();
//                                adapter.getItem(position).setImageId(R.drawable.ic_lock_open);
                            }
                        });
                        alertDialog.setNegativeButton("خیر",null);
                        AlertDialog alert = alertDialog.create();
                        alert.show();
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getFileCount(){
        int fileCount = 0;
        File dir = new File("/storage/emulated/0/OneMob");
        File[] files = dir.listFiles();
        fileCount = files.length;
        return fileCount;
    }

    public String[] getFileName(){
        File dir = new File("/storage/emulated/0/OneMob");
        File[] files = dir.listFiles();
        String[] filesName = new String[files.length];
        for (int i = 0 ; i < files.length ; i++){
            filesName[i] = files[i].getName();
        }
        return filesName;
    }

    private ArrayList<Video> genVideos(int fileCount, String[] videosName, boolean[] videosPurchased) {
        ArrayList<Video> videos = new ArrayList<Video>();
        Video[] arrayVideo = new Video[fileCount];
        for (int k = 0 ; k < arrayVideo.length ; k++){
            if (videosPurchased[k]){
                arrayVideo[k] = new Video("link", videosName[k], R.drawable.ic_lock_open);
            } else {
                arrayVideo[k] = new Video("link", videosName[k], R.drawable.ic_lock_blue);
            }
        }
        for (int i = 0 ; i < fileCount ; i++){
            videos.add(arrayVideo[i]);
        }
        return videos;
    }
}
