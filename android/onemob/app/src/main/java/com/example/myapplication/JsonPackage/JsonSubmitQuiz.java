package com.example.myapplication.JsonPackage;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ShowQuizResultActivity;
import com.example.myapplication.UtilQuiz;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JsonSubmitQuiz extends AsyncTask {

    public static final MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");

    String score = "";
    String httpCode = "";
    String token;
    ArrayList<String> questionsId = new ArrayList<>();
    ArrayList<String> answersId = new ArrayList<>();
    ArrayList<String> trueAnswers = new ArrayList<>();

    Context context;
    TextView lblShowScore;

    public JsonSubmitQuiz(String token) {
        this.token = token;
    }

    public JsonSubmitQuiz(String token, ArrayList<String> questionsId, ArrayList<String> answersId, Context context) {
        this.token = token;
        this.questionsId = questionsId;
        this.answersId = answersId;
        this.context = context;
    }

    public JsonSubmitQuiz(String token, ArrayList<String> questionsId, ArrayList<String> answersId, ArrayList<String> trueAnswers, Context context) {
        this.token = token;
        this.questionsId = questionsId;
        this.answersId = answersId;
        this.trueAnswers = trueAnswers;
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            for (int i = 0 ; i < questionsId.size() ; i++){
                Log.d("questions_id_submit", questionsId.get(i));
                Log.d("answers_id_submit", answersId.get(i));
            }
            String questionIdText = "{\"question_id\": ";
            String answerIdText = ",\"answer_id\": ";
            OkHttpClient okHttpClient = new OkHttpClient.Builder().
                    connectTimeout(15, TimeUnit.SECONDS).
                    writeTimeout(15, TimeUnit.SECONDS).
                    readTimeout(15, TimeUnit.SECONDS).
                    build();
            String[] stringBuilderEachAnswers = new String[questionsId.size()];
            for (int i = 0 ; i < stringBuilderEachAnswers.length ; i++){
                stringBuilderEachAnswers[i] = questionIdText+questionsId.get(i)+answerIdText+answersId.get(i)+"}";
                Log.d("stringBuilders", String.valueOf(stringBuilderEachAnswers[i]));
            }

            StringBuilder result = new StringBuilder("[");
            for (int j = 0 ; j < stringBuilderEachAnswers.length-1 ; j++){
                result.append(stringBuilderEachAnswers[j]+",");
            }
            result.append(stringBuilderEachAnswers[stringBuilderEachAnswers.length-1]+"]");
            Log.d("result_submit", result.toString());

            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("quiz_id", UtilQuiz.quizId)
                    .addFormDataPart("quiz_answers", result.toString())
                    .build();
            Request requestPostSubmit = new Request.Builder()
                    .url("http://138.201.6.240:8000/api/submit-quiz/")
                    .method("POST", body)
                    .addHeader("Authorization", "Token "+token)
                    .addHeader("Cookie", "experimentation_subject_id=eyJfcmFpbHMiOnsibWVzc2FnZSI6IklqZzVaalk1T1RBeExUTmtZekV0TkRjMU5pMWlOR0U0TFRneVlqRXlOVFpsTVRGaU5TST0iLCJleHAiOm51bGwsInB1ciI6ImNvb2tpZS5leHBlcmltZW50YXRpb25fc3ViamVjdF9pZCJ9fQ%3D%3D--8612d03c3621a3786624d23905e2e20eda4a3d8e")
                    .build();
            Response response = null;
            String resultSubmitQuiz = "";
            JSONObject jsonObjectResultSubmitQuiz = null;
            String username = "";
            try {
                response = okHttpClient.newCall(requestPostSubmit).execute();
                httpCode = String.valueOf(response.code());
                resultSubmitQuiz = response.body().string();
                if (httpCode.equals("200")){
                    jsonObjectResultSubmitQuiz = new JSONObject(resultSubmitQuiz);
                    username = jsonObjectResultSubmitQuiz.getString("username");
                    score = jsonObjectResultSubmitQuiz.getString("score");
                }
            } catch (IOException | JSONException e){
                e.printStackTrace();
            }
            Log.d("httpCode", httpCode);
            Log.d("resultSubmitQuiz", resultSubmitQuiz);
            Log.d("username_submit", username);
            Log.d("score_score", score);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            String[] answersStringArray = new String[answersId.size()];
            for (int i = 0 ; i < answersId.size() ; i++){
                int temp = Integer.valueOf(answersId.get(i)) - ((Integer.valueOf(questionsId.get(i))-1)*4);
                answersStringArray[i] = String.valueOf(temp);
            }
            String[] trueAnswersArray = new String[answersId.size()];
            for (int i = 0 ; i < answersId.size() ; i++){
                int temp = Integer.valueOf(trueAnswers.get(i)) - ((Integer.valueOf(questionsId.get(i))-1)*4);
                trueAnswersArray[i] = String.valueOf(temp);
            }
            Intent intentToShowScore = new Intent(context, ShowQuizResultActivity.class);
            intentToShowScore.putExtra("score", score);
            intentToShowScore.putExtra("answersArray", answersStringArray);
            intentToShowScore.putExtra("trueAnswersArray", trueAnswersArray);
            context.startActivity(intentToShowScore);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
