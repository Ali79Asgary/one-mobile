package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceControl;
import android.widget.TextView;

import com.example.myapplication.JsonPackage.JsonPostRegistration;
import com.example.myapplication.JsonPackage.JsonVideoList;
import com.example.myapplication.JsonPackage.JsonVideosDownload;
import com.example.myapplication.ui.exams.ExamsFragment;
import com.example.myapplication.ui.notifications.NotificationsFragment;
import com.example.myapplication.ui.videos.VideosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    String token;
    String path;
    File file;
    int balance = 0;
    TextView lblBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            BottomNavigationView navView = findViewById(R.id.nav_view);
            balance = UtilBalance.balance;
//            lblBalance = findViewById(R.id.lblBalance);
//            lblBalance.setText(String.valueOf(balance));
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
//        path = MainActivity.this.getExternalFilesDir(null).getAbsolutePath();
//        Log.d("fillllll", path);
//        file = MainActivity.this.getExternalFilesDir(null);
////        file = new File(path+"/oneMob/");
//        if (!file.exists()){
//            file.mkdirs();
//            Log.d("FILE NOT EXISTS", file.getAbsolutePath());
//        } else {
//            Log.d("FILE EXISTS", file.getAbsolutePath());
//        }

            try {
                File downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                File[] downloadList = downloadFile.listFiles();
                int downloadCount = downloadList.length;
                Log.d("downloadCount", String.valueOf(downloadCount));
            } catch (NullPointerException e){
                e.printStackTrace();
            }

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
            try {
                try {
                    Log.d("fileMain", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                    token = UtilToken.token;
                    Log.d("tokenUtil", token);
                    Intent intent  = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("tokenLogin", token);
                } catch (NullPointerException e){
                    e.printStackTrace();
                    Log.e("NullPointerException!", e.getMessage());
                }
            } catch (Exception e){
                e.printStackTrace();
                Log.e("Whole Exception!", e.getMessage());
            }
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_videos, R.id.navigation_exams, R.id.navigation_moreOptions).build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

