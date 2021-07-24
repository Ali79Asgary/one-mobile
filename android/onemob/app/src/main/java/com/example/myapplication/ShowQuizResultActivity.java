package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.ui.exams.ExamsFragment;

import java.util.ArrayList;

public class ShowQuizResultActivity extends AppCompatActivity {

    TextView lblShowScore;
    ListView showQuizResultListView;

    String score = "";
    String[] answersArray;
    String[] trueAnswersArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_quiz_result);

        lblShowScore = findViewById(R.id.lblShowScore);
        showQuizResultListView = findViewById(R.id.showQuizResultListView);

        score = getIntent().getExtras().getString("score");
        lblShowScore.setText(score);

        answersArray = getIntent().getStringArrayExtra("answersArray");
        for (int i = 0 ; i < answersArray.length ; i++){
            Log.d("answersArray", answersArray[i]);
        }

        trueAnswersArray = getIntent().getStringArrayExtra("trueAnswersArray");
        for (int j = 0 ; j < trueAnswersArray.length ; j++){
            Log.d("trueAnswers", trueAnswersArray[j]);
        }

        int questionCount = answersArray.length;
        ArrayList<QuizResult> quizResultArrayList = genQuizResult(questionCount, answersArray, trueAnswersArray);
        QuizResultListAdapter quizResultListAdapter = new QuizResultListAdapter(ShowQuizResultActivity.this, R.layout.show_result_view_layout, quizResultArrayList);
        showQuizResultListView.setAdapter(quizResultListAdapter);
    }

    private ArrayList<QuizResult> genQuizResult(int questionCount, String[] answersArray, String[] trueAnswersArray){
        ArrayList<QuizResult> quizResults = new ArrayList<>();
        QuizResult[] quizResultArray = new QuizResult[questionCount];
        for (int i = 0 ; i < questionCount ; i++){
            quizResultArray[i] = new QuizResult(answersArray[i], trueAnswersArray[i]);
        }
        for (int j = 0 ; j < questionCount ; j++){
            quizResults.add(quizResultArray[j]);
        }
        return quizResults;
    }

    @Override
    public void onBackPressed() {
        Log.d("ShowQuizResult", "HelloShowQuizResult");
//        ExamsFragment examsFragment = new ExamsFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, examsFragment).commit();

        Intent intent = new Intent(ShowQuizResultActivity.this, MainActivity.class);
        startActivity(intent);
//        getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment, examsFragment).commit();
    }
}