package com.example.myapplication.JsonPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myapplication.Exam;
import com.example.myapplication.ExamListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.ShowExamActivity;
import com.example.myapplication.UtilBalance;
import com.example.myapplication.UtilProgressDialog;
import com.example.myapplication.UtilQuiz;
import com.example.myapplication.UtilToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonGetQuizzes extends AsyncTask {

    String token;
    int responseCode = 0;
    ArrayList<String> quizTitlesArray = new ArrayList<>();
    Activity activity = null;
    Context context = null;
    ListView examsListView;
    TextView lblShowBalance;

    public JsonGetQuizzes(String token, Activity activity, Context context, ListView examsListView, TextView lblShowBalance) {
        this.token = token;
        this.activity = activity;
        this.context = context;
        this.examsListView = examsListView;
        this.lblShowBalance = lblShowBalance;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().
                    connectTimeout(15, TimeUnit.SECONDS).
                    writeTimeout(15, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    build();
            Request request = new Request.Builder().url("http://138.201.6.240:8000/api/quizzes").method("GET", null).addHeader("Authorization", "Token "+token).build();
            Response response = null;
            String resultQuizzesList = "";
            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            try {
                response = okHttpClient.newCall(request).execute();
                resultQuizzesList = response.body().string();
                responseCode = response.code();
                jsonObject = new JSONObject(resultQuizzesList);
                jsonArray = jsonObject.getJSONArray("quiz_titles");
                for (int i = 0 ; i < jsonArray.length() ; i++){
                    quizTitlesArray.add(jsonArray.getString(i));
                }
            } catch (IOException | JSONException e){
                e.printStackTrace();
            }
            Log.d("quiz-titles", String.valueOf(jsonArray));
            for (int j = 0 ; j < quizTitlesArray.size() ; j++){
                Log.d("title-exam", quizTitlesArray.get(j));
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
            if (responseCode == 200) {
                updateBalance();
                Toasty.success(context, "دریافت آزمونها موفقیت آمیز بود!", Toast.LENGTH_LONG).show();
                int examsCount = quizTitlesArray.size();
                ArrayList<Exam> examList = genExams(examsCount, quizTitlesArray);
                ExamListAdapter examListAdapter = new ExamListAdapter(activity, R.layout.exams_view_layout, examList);
                examsListView.setAdapter(examListAdapter);
                examsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            updateBalance();
                            showProgressDialog(UtilProgressDialog.progressDialog);
                            UtilQuiz.quizId = String.valueOf(position+1);
                            JsonShowQuiz jsonShowQuiz = (JsonShowQuiz) new JsonShowQuiz(
                                    quizTitlesArray.get(position),
                                    UtilToken.token).
                                    execute();
                            Intent intentGoToShowExam = new Intent(context, ShowExamActivity.class);
                            intentGoToShowExam.putExtra("titlesArray", quizTitlesArray.get(position));
                            activity.startActivity(intentGoToShowExam);
                            dismissProgressDialog(UtilProgressDialog.progressDialog);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
                            dismissProgressDialog(UtilProgressDialog.progressDialog);
                        }
                    }
                });
                dismissProgressDialog(UtilProgressDialog.progressDialog);
            } else {
                updateBalance();
                dismissProgressDialog(UtilProgressDialog.progressDialog);
                Toasty.error(context, "دریافت ویدیوها موفقیت آمیز نبود!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            dismissProgressDialog(UtilProgressDialog.progressDialog);
            Toasty.error(context, "خطایی رخ داده است!", Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<Exam> genExams(int examCount, ArrayList<String> quizTitlesArray){
        ArrayList<Exam> exams = new ArrayList<Exam>();
        Exam[] examsArray = new Exam[examCount];
        for (int i = 0 ; i < examCount ; i++){
            examsArray[i] = new Exam("examLink", quizTitlesArray.get(i));
        }
        for (int j = 0 ; j < examCount ; j++){
            exams.add(examsArray[j]);
        }
        return exams;
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
