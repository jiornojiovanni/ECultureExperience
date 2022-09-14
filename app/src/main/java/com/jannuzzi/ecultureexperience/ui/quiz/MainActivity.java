package com.jannuzzi.ecultureexperience.ui.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView tv_question;
    Button b_answer1, b_answer2, b_answer3, b_answer4;

    List<QuestionItem> questionItems;
    int currentQuestion = 0;
    int correct = 0, wrong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_question = findViewById(R.id.question);
        b_answer1 = findViewById(R.id.answer1);
        b_answer2 = findViewById(R.id.answer2);
        b_answer3 = findViewById(R.id.answer3);
        b_answer4 = findViewById(R.id.answer4);

        parsePathFile(readPathFile("questions.json"));
        setQuestionsScreen(currentQuestion);

        b_answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionItems.get(currentQuestion).getAnswer1()
                        .equals(questionItems.get(currentQuestion).getCorrect())) {
                    correct++;
                    Toast.makeText(MainActivity.this, "Correct", Toast.LENGTH_SHORT).show();

                } else {
                    wrong++;
                    Toast.makeText(MainActivity.this, "Wrong", Toast.LENGTH_SHORT).show();

                }
                if (currentQuestion < questionItems.size() - 1) {
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
        });

        b_answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionItems.get(currentQuestion).getAnswer2()
                        .equals(questionItems.get(currentQuestion).getCorrect())) {
                    correct++;
                    Toast.makeText(MainActivity.this, "Correct", Toast.LENGTH_SHORT).show();

                } else {
                    wrong++;
                    Toast.makeText(MainActivity.this, "Wrong", Toast.LENGTH_SHORT).show();

                }
                if (currentQuestion < questionItems.size() - 1) {
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
        });

        b_answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionItems.get(currentQuestion).getAnswer3()
                        .equals(questionItems.get(currentQuestion).getCorrect())) {
                    correct++;
                    Toast.makeText(MainActivity.this, "Correct", Toast.LENGTH_SHORT).show();

                } else {
                    wrong++;
                    Toast.makeText(MainActivity.this, "Wrong", Toast.LENGTH_SHORT).show();

                }
                if (currentQuestion < questionItems.size() - 1) {
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
        });

        b_answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (questionItems.get(currentQuestion).getAnswer4()
                        .equals(questionItems.get(currentQuestion).getCorrect())) {
                    correct++;
                    Toast.makeText(MainActivity.this, "Correct", Toast.LENGTH_SHORT).show();

                } else {
                    wrong++;
                    Toast.makeText(MainActivity.this, "Wrong", Toast.LENGTH_SHORT).show();

                }
                if (currentQuestion < questionItems.size() - 1) {
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
        });
    }

    private void setQuestionsScreen(int number) {
        tv_question.setText(questionItems.get(number).getQuestion());
        b_answer1.setText(questionItems.get(number).getAnswer1());
        b_answer2.setText(questionItems.get(number).getAnswer2());
        b_answer3.setText(questionItems.get(number).getAnswer3());
        b_answer4.setText(questionItems.get(number).getAnswer4());

    }

    private List<QuestionItem> parsePathFile(InputStream content) {
        JsonReader reader = new JsonReader(new InputStreamReader(content));
        questionItems = new ArrayList<>();
        try {
            reader.beginArray();

            while (reader.hasNext()) {
                String question = "";
                String answer1 = "";
                String answer2 = "";
                String answer3 = "";
                String answer4 = "";
                String correct = "";

                reader.beginObject();
                while (reader.hasNext()) {
                    String token = reader.nextName();
                    if ("question".equals(token)) {
                        question = reader.nextString();
                    } else if ("answer1".equals(token)) {
                        answer1 = reader.nextString();
                    } else if ("answer2".equals(token)) {
                        answer2 = reader.nextString();

                    } else if ("answer3".equals(token)) {
                        answer3 = reader.nextString();

                    } else if ("answer4".equals(token)) {
                        answer4 = reader.nextString();

                    } else if ("correct".equals(token)) {
                        correct = reader.nextString();

                    } else {
                        reader.skipValue();
                    }

                    questionItems.add(new QuestionItem(
                            question,
                            answer1,
                            answer2,
                            answer3,
                            answer4,
                            correct
                    ));
                }

                reader.endObject();

            }

            return questionItems;
        } catch (IOException e) {
            e.printStackTrace();
            return questionItems;
        }
    }

    private InputStream readPathFile(String name) {
        File file = new File(Environment.getExternalStorageDirectory()
                + "/Download/" + name);
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}