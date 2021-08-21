package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class ShowVideoThirdActivity extends AppCompatActivity {

    VideoView videoView;
    String videoLink = "http://138.201.6.240:8000/api/video/";
    String videoLink1 = "https://filesamples.com/samples/video/m4v/sample_960x400_ocean_with_audio.m4v";
    Uri videoUri;
    Intent intent = getIntent();
    String videoTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video_third);
        try {
            videoTitle = getIntent().getStringExtra("VideoTitleList");
            Log.d("VideoTitleList", videoTitle);
            videoLink = videoLink.concat(videoTitle).concat("/").concat(UtilToken.token);
            Log.d("VideoLink", videoLink);
            videoUri = Uri.parse(videoLink1);
            videoView = findViewById(R.id.videoViewShowVideos3);
            videoView.setVideoURI(videoUri);
            videoView.start();
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}