package com.example.myapplication.JsonPackage;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonShowQuiz extends AsyncTask {

    ArrayList<JSONObject> questionsJsonObjectArray = new ArrayList<>();
    ArrayList<JSONObject> question = new ArrayList<>();
    ArrayList<String> questionsTextArray = new ArrayList<>();
    ArrayList<JSONArray> answers = new ArrayList<>();
    JSONObject[][] eachAnswer = null;
    String[][] answersText = null;
    int[] examsId = null;
    String[] questionsText = null;
    String quizTitle = "";
    String token = "";

    EditText editTextQuestion;

    public JsonShowQuiz(String quizTitle, String token) {
        this.quizTitle = quizTitle;
        this.token = token;
    }

    public JsonShowQuiz(String quizTitle, String token, EditText editTextQuestion) {
        this.quizTitle = quizTitle;
        this.token = token;
        this.editTextQuestion = editTextQuestion;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().
                    connectTimeout(15, TimeUnit.SECONDS).
                    writeTimeout(15, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    build();
            Request request = new Request.Builder().url("http://138.201.6.240:8000/api/get-quiz/"+quizTitle).method("GET", null).addHeader("Authorization", "Token "+token).build();
            Response response = null;
            String resultShowExam = "";
            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            JSONArray jsonArrayQuestion = null;
            try {
                response = okHttpClient.newCall(request).execute();
                resultShowExam = response.body().string();
                jsonObject = new JSONObject(resultShowExam);
                jsonArray = jsonObject.getJSONArray("quiz");

                eachAnswer = new JSONObject[jsonArray.length()][4];
                answersText = new String[jsonArray.length()][4];
                for (int i = 0 ; i < jsonArray.length() ; i++){
                    questionsJsonObjectArray.add(jsonArray.getJSONObject(i));
                    question.add(questionsJsonObjectArray.get(i).getJSONObject("question"));
                    questionsTextArray.add(question.get(i).getString("text"));
                    answers.add(questionsJsonObjectArray.get(i).getJSONArray("answers"));
                    for (int j = 0 ; j < 4 ; j++){
                        eachAnswer[i][j] = answers.get(i).getJSONObject(j);
                        answersText[i][j] = eachAnswer[i][j].getString("text");
                    }
                }
            } catch (IOException | NullPointerException e){
                e.printStackTrace();
            }
            Log.d("resultShowExam", resultShowExam);
            Log.d("quiz_array", String.valueOf(jsonArray));
            for (int i = 0 ; i < jsonArray.length() ; i++){
                Log.d("question", String.valueOf(questionsJsonObjectArray.get(i)));
                Log.d("question-rrrr", String.valueOf(question.get(i)));
                Log.d("questions-text", String.valueOf(questionsTextArray.get(i)));
                Log.d("answers", String.valueOf(answers.get(i)));
                for (int j = 0 ; j < 4 ; j++){
                    Log.d("eachAnswers", String.valueOf(eachAnswer[i][j]));
                    Log.d("answers-text", String.valueOf(answersText[i][j]));
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
//        editTextQuestion.setText(questionsText[0]);
    }
}
