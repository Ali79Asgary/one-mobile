package com.example.myapplication.ui.videos;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.JsonPackage.JsonGetBalance;
import com.example.myapplication.JsonPackage.JsonVideoList;
import com.example.myapplication.R;
import com.example.myapplication.UtilBalance;
import com.example.myapplication.UtilToken;

public class VideosFragment extends Fragment {

    private VideosViewModel videosViewModel;
    ListView videoListView;
    String token = "token";
    ImageView lockVideoImageView;
    TextView lblShowBalance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        videosViewModel = ViewModelProviders.of(this).get(VideosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_videos, container, false);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},111);

        try {
            updateBalance();
            videoListView = root.findViewById(R.id.videosListView);
            lockVideoImageView = root.findViewById(R.id.lockVideoImageView);
            lblShowBalance = root.findViewById(R.id.lblBalanceVideo);
            lblShowBalance.setText(String.valueOf(UtilBalance.balance));
            try {
                token = UtilToken.token;
                Log.d("tokenFromArg", token);
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            JsonVideoList jsonVideoList = (JsonVideoList) new JsonVideoList(token, getActivity(), getContext(), videoListView, lockVideoImageView, lblShowBalance).execute();
        } catch (Exception e){
            e.printStackTrace();
        }
        return root;
    }

    //Download video with URL and show this progress in the notification and after downloading , push videos into the Download folder in device.
    public void startDownloading(String url){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Video");
        request.setDescription("Video Downloading!");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , ""+System.currentTimeMillis());
        DownloadManager downloadManager = (DownloadManager) getActivity().getBaseContext().getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                } else {
                    Toast.makeText(getContext(), "Permission denied...!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void updateBalance() {
        try {
            JsonGetBalance jsonGetBalance = (JsonGetBalance) new JsonGetBalance(UtilToken.token).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}