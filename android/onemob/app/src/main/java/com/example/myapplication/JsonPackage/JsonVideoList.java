package com.example.myapplication.JsonPackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myapplication.ExoPlayer;
import com.example.myapplication.R;
import com.example.myapplication.UtilBalance;
import com.example.myapplication.UtilContentLengths;
import com.example.myapplication.UtilProgressDialog;
import com.example.myapplication.UtilToken;
import com.example.myapplication.Video;
import com.example.myapplication.VideoListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonVideoList extends AsyncTask {

    ArrayList<JSONObject> videosJsonObjectArrayList = new ArrayList<>();
    String[] videosName = null;
    String[] videosTitle = null;
    int[] videosPrice = null;
    boolean[] videosPurchased = null;
    String token = "";
    int responseCode = 0;
    Activity activity = null;
    Context context = null;
    ListView videosListView;
    String path;
    File file;
    ImageView lockVideoImageView;
    TextView lblShowBalance;
    ProgressDialog progressDialog;

    public JsonVideoList(
            String token,
            Activity activity,
            Context context,
            ListView videosListView,
            ImageView lockVideoImageView,
            TextView lblShowBalance,
            ProgressDialog progressDialog) {
        this.token = token;
        this.activity = activity;
        this.context = context;
        this.videosListView = videosListView;
        this.lockVideoImageView = lockVideoImageView;
        this.lblShowBalance = lblShowBalance;
        this.progressDialog = progressDialog;
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
            OkHttpClient client = new OkHttpClient.Builder().
                    connectTimeout(15, TimeUnit.SECONDS).
                    writeTimeout(15, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    build();
            Request request = new Request.Builder().url("http://138.201.6.240:8000/api/videos/").method("GET", null).addHeader("Authorization", "Token "+token).build();
            Response response = null;
            String resultVideoList = "";
            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            JSONObject video1 = null;
            String video1Name = "";
            int jsonArrayLength = 0;
            try {
                response = client.newCall(request).execute();
                responseCode = response.code();
                Log.d("responseCodeVideoList", String.valueOf(responseCode));
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
            updateBalance();
//            progressDialog = new ProgressDialog(context);
//            showProgressDialog(progressDialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            if (responseCode == 200) {
                updateBalance();
                Toasty.success(context, "دریافت ویدیوها موفقیت آمیز بود!", Toast.LENGTH_LONG).show();
                int fileCount = videosName.length;
                Log.d("fileCount", String.valueOf(fileCount));
                if (UtilContentLengths.contentLengths.size() < fileCount){
                    for (int i = UtilContentLengths.contentLengths.size() ; i < fileCount ; i++){
                        UtilContentLengths.contentLengths.add(i, "");
                    }
                }
                ArrayList<Video> videosList = genVideos(fileCount, videosTitle, videosPurchased);
                final VideoListAdapter adapter = new VideoListAdapter(activity, R.layout.videos_view_layout, videosList);
                videosListView.setAdapter(adapter);
                videosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        try {
                            if (videosPurchased[position]){
                                Intent toShowVideo = new Intent(context, ExoPlayer.class);
                                toShowVideo.putExtra("tokenShowVideo", token);
                                toShowVideo.putExtra("VideoTitleList", videosTitle[position]);
                                toShowVideo.putExtra("videosTitle", videosTitle);
                                toShowVideo.putExtra("videosName", videosName);
                                toShowVideo.putExtra("VideoName", videosName[position]);
                                toShowVideo.putExtra("position", position);
                                context.startActivity(toShowVideo);
                                dismissProgressDialog(UtilProgressDialog.progressDialog);
                            } else {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setTitle("فیلم                                                       ");
                                alertDialog.setMessage("آیا مایل به خرید این فیلم هستید؟");
                                alertDialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            updateBalance();
                                            showProgressDialog(UtilProgressDialog.progressDialog);
                                            JsonBuyVideo jsonBuyVideo = (JsonBuyVideo) new JsonBuyVideo(
                                                    UtilToken.token,
                                                    videosTitle[position],
                                                    activity,
                                                    context,
                                                    videosListView,
                                                    lockVideoImageView,
                                                    lblShowBalance,
                                                    UtilProgressDialog.progressDialog).
                                                    execute();
                                            dismissProgressDialog(UtilProgressDialog.progressDialog);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
                                            UtilProgressDialog.progressDialog.dismiss();
                                        }
                                    }
                                });
                                alertDialog.setNegativeButton("خیر",null);
                                AlertDialog alert = alertDialog.create();
                                alert.show();
                            }
                            dismissProgressDialog(UtilProgressDialog.progressDialog);
                            UtilProgressDialog.progressDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });
                dismissProgressDialog(UtilProgressDialog.progressDialog);
            } else {
                updateBalance();
                dismissProgressDialog(UtilProgressDialog.progressDialog);
                Toasty.error(context, "دریافت ویدیوها موفقیت آمیز نبود!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
            Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
            UtilProgressDialog.progressDialog.dismiss();
        }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showProgressDialog(ProgressDialog progressDialog) {
        try {
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.create();
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissProgressDialog(ProgressDialog progressDialog) {
        try {
            progressDialog.dismiss();
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
