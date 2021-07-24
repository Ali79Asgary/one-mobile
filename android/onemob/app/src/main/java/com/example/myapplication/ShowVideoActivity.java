package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.potyvideo.library.AndExoPlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class ShowVideoActivity extends AppCompatActivity {

    VideoView videoViewShow;
    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;
    AndExoPlayerView andExoPlayerView;
    String[] videosTitle;
    String[] videosName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        try {
            videoViewShow = findViewById(R.id.videoViewShow);
//            andExoPlayerView = findViewById(R.id.exoPLayerView);
            String token = getIntent().getExtras().getString("tokenShowVideo");
            String videoTitle = getIntent().getExtras().getString("VideoTitleList");
            int position = getIntent().getExtras().getInt("position");
            videosTitle = getIntent().getExtras().getStringArray("videosTitle");
            videosName = getIntent().getExtras().getStringArray("videosName");
            ShowVideoAsyncTask showVideoAsyncTask = (ShowVideoAsyncTask) new ShowVideoAsyncTask(andExoPlayerView, exoPlayerView, exoPlayer, position, videoTitle, token, videoViewShow, ShowVideoActivity.this, videosTitle, videosName).execute();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Get video's path from VideosFragment class and show video in a VideoView.
    public void showVideo(String key, VideoView videoViewShow){
        String path = getIntent().getStringExtra(key);
        videoViewShow.setVideoPath(path);
        videoViewShow.start();
        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoViewShow);
        videoViewShow.setMediaController(controller);
    }

    public void secondShowVideo(File file, VideoView videoViewShow, Context context){
        videoViewShow.setVideoPath(file.getPath());
        videoViewShow.start();
        MediaController controller = new MediaController(context);
        controller.setAnchorView(videoViewShow);
        videoViewShow.setMediaController(controller);
    }
}

class ShowVideoAsyncTask extends AsyncTask{

    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;

    AndExoPlayerView andExoPlayerView;

    int position = 0;

    boolean[] isDownloaded;

    String[] contentLength;

    String videoTitle = "video";
    String fileName = "Ali";
    String token = "";
    Response response = null;
    VideoView videoView = null;
    Context context = null;
    File file = null;
    File root;
    FileOutputStream fileOutputStream;
    String path;

    String[] videosTitle;
    String[] videosName;

    public ShowVideoAsyncTask(String token,String videoTitle, VideoView videoView, Context context) {
        this.token = token;
        this.videoTitle = videoTitle;
        this.videoView = videoView;
        this.context = context;
    }

    public ShowVideoAsyncTask(int position, String videoTitle, String token, VideoView videoView, Context context, String[] videosTitle, String[] videosName) {
        this.position = position;
        this.videoTitle = videoTitle;
        this.token = token;
        this.videoView = videoView;
        this.context = context;
        this.videosTitle = videosTitle;
        this.videosName = videosName;
    }

    public ShowVideoAsyncTask(AndExoPlayerView andExoPlayerView, SimpleExoPlayerView exoPlayerView, SimpleExoPlayer exoPlayer, int position, String videoTitle, String token, VideoView videoView, Context context, String[] videosTitle, String[] videosName) {
        this.andExoPlayerView = andExoPlayerView;
        this.exoPlayerView = exoPlayerView;
        this.exoPlayer = exoPlayer;
        this.position = position;
        this.videoTitle = videoTitle;
        this.token = token;
        this.videoView = videoView;
        this.context = context;
        this.videosTitle = videosTitle;
        this.videosName = videosName;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            path = context.getExternalFilesDir(null).getAbsolutePath();
            file = new File(path);
            if (!file.exists()){
                file.mkdirs();
                Log.d("FILE NOT EXISTS", file.getAbsolutePath());
            } else {
                Log.d("FILE EXISTS", file.getAbsolutePath());
            }
            File rootFile = new File(file, fileName);
            File newNameFile = new File(rootFile, "sss");
            File[] fileList = file.listFiles();
            for (int i = 0 ; i < fileList.length ; i++){
                Log.d("listOne4444444", fileList[i].getAbsolutePath());
                Log.d("listOneName", fileList[i].getName());
                fileList[i].renameTo(newNameFile);
                Log.d("listOneNameNew", fileList[i].getName());
            }

            isDownloaded = new boolean[videosName.length];
            for (int k = 0 ;  k < isDownloaded.length ; k++){
                isDownloaded[k] = false;
            }

            for (int l = 0 ; l < fileList.length ; l++){
                for (int s = 0 ; s < videosName.length ; s++){
                    if (fileList[l].getName().equals(videosName[s])){
                        isDownloaded[s] = true;
                        Log.d("truth", String.valueOf(isDownloaded[s]));
                    } else {
//                        isDownloaded[s] = false;
                        Log.d("false", String.valueOf(isDownloaded[s]));
                    }
                }
            }

            if (isDownloaded[position]){
                Log.d("isDownloaded", "true");
            } else {
                Log.d("isDownloaded", "false");
                OkHttpClient client = new OkHttpClient.Builder().build();
                Request request = new Request.Builder().url("http://138.201.6.240:8000/api/video/"+videoTitle).method("GET", null).addHeader("Authorization", "Token "+token).build();
                response = null;
                String result = "";
                try {
//                path = "/storage/emulated/0/OneMob";
                    path = context.getExternalFilesDir(null).getAbsolutePath();
                    file = new File(path);
                    if (!file.exists()){
                        file.mkdirs();
                        Log.d("FILE EXISTS", file.getAbsolutePath());
                    }
//                root = new File(path);
                    contentLength = new String[videosName.length];
                    root = new File(file, videosName[position]);
                    response = client.newCall(request).execute();
                    Headers headers = response.headers();
                    List<String> content_length = response.headers("Content-Length");
                    contentLength[position] = content_length.get(0);
                    UtilContentLengths.contentLengths.add(position, content_length.get(0));
                    Log.d("Content_length", UtilContentLengths.contentLengths.get(position));
                    Log.d("Headers", String.valueOf(headers));
                    Log.d("Content_lengths", contentLength[position]);
                    Log.d("Content_length", String.valueOf(content_length));
//                    result = response.body().string();

//                    Log.d("Content_length", String.valueOf(result));
                    fileOutputStream = new FileOutputStream(root);
                    fileOutputStream.write(response.body().bytes());
                    fileOutputStream.close();
                    response.body().close();
                    File newName = new File(root, "sss");
                    File[] listOne = file.listFiles();
                    for (int i = 0 ; i < listOne.length ; i++){
                        Log.d("listOne4444444", listOne[i].getAbsolutePath());
                        Log.d("listOneName", listOne[i].getName());
                        listOne[i].renameTo(newName);
                        Log.d("listOneNameNew", listOne[i].getName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            Log.d("temp0", "temp");
            Log.d("fileVideoLength", String.valueOf(UtilContentLengths.contentLengths.get(position)));
            Log.d("temp1", "temp");
            File[] fileThird = new File[videosName.length];
            for (int x = 0 ; x < videosName.length ; x++){
                fileThird[x] = new File(path+"/"+videosName[x]);
            }
            File fileSecond = new File(path+"/"+videosName[position]);
            Log.d("file", file.getAbsolutePath());
            Log.d("fileSecondWWWW", fileSecond.getAbsolutePath());
            Log.d("fileSecondLength", String.valueOf(fileSecond.length()));
            Log.d("fileVideoLength", String.valueOf(UtilContentLengths.contentLengths.get(position)));
//            Log.d("fileOutputStream", fileOutputStream.toString());
            String fileLength = String.valueOf(fileSecond.length());
            if (fileLength.equals(UtilContentLengths.contentLengths.get(position))){
                Uri uri = Uri.parse(fileSecond.getAbsolutePath());
//                andExoPlayerView.setBackgroundColor(Color.BLACK);
//                andExoPlayerView.setSource(uri.toString());
//                initializePlayer(fileSecond.getAbsolutePath());


//                videoView.setVideoPath(fileSecond.getAbsolutePath());
                videoView.setVideoURI(uri);
                videoView.start();
//                videoView.setBackgroundColor(Color.BLACK);
                MediaController controller = new MediaController(context);
//                controller.setMediaPlayer(videoView);
//                controller.setAnchorView(videoView);
                videoView.setMediaController(controller);
            } else {
                Toast.makeText(context, "فایل به درستی دانلود نشده است. لطفا دوباره امتحان کنید!", Toast.LENGTH_LONG).show();
                if (fileSecond.delete()){
                    Log.d("fileSecondDeleteSuccess", "File Deleted!");
                } else {
                    Log.d("fileSecondDeleteFailed", "File Not Deleted!");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initializePlayer(String path){
// Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//Initialize the player
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
//Initialize simpleExoPlayerView
        exoPlayerView.setPlayer(exoPlayer);
        exoPlayerView.setBackgroundColor(Color.BLACK);
// Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "A.A"));
// Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
// This is the MediaSource representing the media to be played.
        Uri videoUri = Uri.parse(path);
        MediaSource videoSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
// Prepare the player with the source.
        exoPlayer.prepare(videoSource);
    }
}