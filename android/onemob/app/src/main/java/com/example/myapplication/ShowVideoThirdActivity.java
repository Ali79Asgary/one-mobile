package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class ShowVideoThirdActivity extends AppCompatActivity {

    VideoView videoView;
    String videoLink = "http://138.201.6.240:8000/api/video/ksnn_compilation_master_the_internet_512kb.mp4";
    Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video_third);
        try {
            Log.d("hello", "hello");
            videoUri = Uri.parse(videoLink);
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