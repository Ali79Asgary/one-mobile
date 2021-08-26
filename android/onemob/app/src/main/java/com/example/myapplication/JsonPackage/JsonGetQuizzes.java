package com.example.myapplication.JsonPackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.myapplication.Exam;
import com.example.myapplication.ExamListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.ShowExamActivity;
import com.example.myapplication.UtilQuiz;
import com.example.myapplication.UtilToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonGetQuizzes extends AsyncTask {

    String token;
    ArrayList<String> quizTitlesArray = new ArrayList<>();
    Activity activity = null;
    Context context = null;
    ListView examsListView;

    public JsonGetQuizzes(String token) {
        this.token = token;
    }

    public JsonGetQuizzes(String token, Activity activity, Context context, ListView examsListView) {
        this.token = token;
        this.activity = activity;
        this.context = context;
        this.examsListView = examsListView;
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
        int examsCount = quizTitlesArray.size();
        ArrayList<Exam> examList = genExams(examsCount, quizTitlesArray);
        ExamListAdapter examListAdapter = new ExamListAdapter(activity, R.layout.exams_view_layout, examList);
        examsListView.setAdapter(examListAdapter);
        examsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UtilQuiz.quizId = String.valueOf(position+1);
                JsonShowQuiz jsonShowQuiz = (JsonShowQuiz) new JsonShowQuiz(quizTitlesArray.get(position), UtilToken.token).execute();
                Intent intentGoToShowExam = new Intent(context, ShowExamActivity.class);
                intentGoToShowExam.putExtra("titlesArray", quizTitlesArray.get(position));
                activity.startActivity(intentGoToShowExam);
            }
        });
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
}
