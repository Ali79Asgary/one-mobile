package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection.Factory;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.TransferListener;

public class ExoPlayer extends AppCompatActivity {

    PlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;
    Context context;
    ProgressBar progressBar;
    String videoLink = "http://138.201.6.240:8000/api/video/";
    String videoURL = "https://filesamples.com/samples/video/m4v/sample_960x400_ocean_with_audio.m4v";
    String videoTitle = "";
    String videoName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);
        try {
            videoTitle = getIntent().getStringExtra("VideoTitleList");
            videoName = getIntent().getStringExtra("VideoName");
            Log.d("VideoTitleListExo", videoTitle);
            videoLink = videoLink.concat(videoName).concat("/").concat(UtilToken.token);
            Log.d("VideoLinkExo", videoLink);
            exoPlayerView = findViewById(R.id.exo_player);
            progressBar = findViewById(R.id.progress_circular);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            Uri videoUri = Uri.parse(videoLink);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory( "exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
            if (exoPlayer.getPlaybackState() == Player.STATE_BUFFERING) {
                progressBar.setVisibility(View.VISIBLE);
                exoPlayer.setPlayWhenReady(true);
            } else if (exoPlayer.getPlaybackState() == Player.STATE_READY){
                progressBar.setVisibility(View.INVISIBLE);
                exoPlayer.setPlayWhenReady(true);
            } else if (exoPlayer.getPlaybackState() == Player.STATE_IDLE) {
                progressBar.setVisibility(View.VISIBLE);
                exoPlayer.setPlayWhenReady(true);
            } else if (exoPlayer.getPlaybackState() == Player.STATE_ENDED) {
                progressBar.setVisibility(View.INVISIBLE);
                exoPlayer.setPlayWhenReady(true);
                exoPlayer.stop();
            }
            exoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    try {
                        if (playbackState == Player.STATE_BUFFERING) {
                            progressBar.setVisibility(View.VISIBLE);
                            exoPlayer.setPlayWhenReady(true);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    try {
                        System.out.println(error.getMessage());
                        exoPlayer.retry();
                        exoPlayer.setPlayWhenReady(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        try {
            System.out.println("On Stop");
            exoPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            System.out.println("On Destroy");
            exoPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}