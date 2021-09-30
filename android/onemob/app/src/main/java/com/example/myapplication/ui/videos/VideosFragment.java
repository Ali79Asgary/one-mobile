package com.example.myapplication.ui.videos;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.JsonPackage.JsonGetBalance;
import com.example.myapplication.JsonPackage.JsonVideoList;
import com.example.myapplication.R;
import com.example.myapplication.UtilBalance;
import com.example.myapplication.UtilLblShowBalance;
import com.example.myapplication.UtilProgressDialog;
import com.example.myapplication.UtilToken;

import es.dmoral.toasty.Toasty;

public class VideosFragment extends Fragment {

    private VideosViewModel videosViewModel;
    ListView videoListView;
    ImageView lockVideoImageView;
    TextView lblShowBalance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        videosViewModel = ViewModelProviders.of(this).get(VideosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_videos, container, false);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
        try {
            UtilProgressDialog.progressDialog = new ProgressDialog(getContext());
            UtilProgressDialog.progressDialog.setMessage("Loading...");
            UtilProgressDialog.progressDialog.setCancelable(false);
            UtilProgressDialog.progressDialog.setCanceledOnTouchOutside(false);
            UtilProgressDialog.progressDialog.create();
            UtilProgressDialog.progressDialog.show();
            setUI(root);
            UtilLblShowBalance.lblShowBalance = lblShowBalance;
            updateBalance();
            JsonVideoList jsonVideoList = (JsonVideoList) new JsonVideoList(
                    UtilToken.token,
                    getActivity(),
                    getContext(),
                    videoListView,
                    lockVideoImageView,
                    lblShowBalance,
                    UtilProgressDialog.progressDialog).
                    execute();
        } catch (Exception e){
            e.printStackTrace();
            updateBalance();
            Toasty.error(getContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
        return root;
    }

    private void setUI(View root) {
        try {
            videoListView = root.findViewById(R.id.videosListView);
            lockVideoImageView = root.findViewById(R.id.lockVideoImageView);
            lblShowBalance = root.findViewById(R.id.lblBalanceVideo);
            lblShowBalance.setText(String.valueOf(UtilBalance.balance));
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(getContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }

    public void updateBalance() {
        try {
            JsonGetBalance jsonGetBalance = (JsonGetBalance) new JsonGetBalance(UtilToken.token).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(getContext(), "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }
}