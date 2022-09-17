package com.jannuzzi.ecultureexperience.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.data.JSONParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    TextView tv_question;
    Button b_answer1, b_answer2, b_answer3, b_answer4;

    List<Question> questionItems;
    int currentQuestion = 0;
    int correct = 0, wrong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String questionFile = getIntent().getExtras().getString("Name");
        View.OnClickListener listener = view -> {

            Button button = (Button) view;

            if (button.getText() == questionItems.get(currentQuestion).getCorrectAnswer().getText()) {
                Toast.makeText(getApplicationContext(), R.string.correct, Toast.LENGTH_SHORT).show();
                correct++;
                advanceQuiz();
            } else {
                Toast.makeText(getApplicationContext(), R.string.wrong, Toast.LENGTH_SHORT).show();
                wrong++;
                advanceQuiz();
            }
        };

        FirebaseStorage.getInstance().getReference("Questions").child(questionFile).getBytes(Long.MAX_VALUE)
                .addOnSuccessListener(task -> {
                    setupUI();
                    ByteArrayInputStream stream = new ByteArrayInputStream(task);

                    questionItems = JSONParser.parseQuestions(stream);
                    if (questionItems == null) {
                        finish();
                    }
                    setQuestionsScreen(currentQuestion);
                    b_answer1.setOnClickListener(listener);
                    b_answer2.setOnClickListener(listener);
                    b_answer3.setOnClickListener(listener);
                    b_answer4.setOnClickListener(listener);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), R.string.qr_invalid, Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void setupUI() {
        setContentView(R.layout.activity_quiz);

        tv_question = findViewById(R.id.question);
        b_answer1 = findViewById(R.id.answer1);
        b_answer2 = findViewById(R.id.answer2);
        b_answer3 = findViewById(R.id.answer3);
        b_answer4 = findViewById(R.id.answer4);
    }

    private void advanceQuiz() {
        if (currentQuestion < 3) {
            currentQuestion++;
            setQuestionsScreen(currentQuestion);
        } else {
            Intent intent = new Intent(getApplicationContext(), EndActivity.class);
            intent.putExtra("correct", correct);
            intent.putExtra("wrong", wrong);
            startActivity(intent);
            finish();
        }
    }

    private void setQuestionsScreen(int number) {
        tv_question.setText(questionItems.get(number).getQuestionText());
        b_answer1.setText(questionItems.get(number).getAnswers().get(0).getText());
        b_answer2.setText(questionItems.get(number).getAnswers().get(1).getText());
        b_answer3.setText(questionItems.get(number).getAnswers().get(2).getText());
        b_answer4.setText(questionItems.get(number).getAnswers().get(3).getText());
    }
}