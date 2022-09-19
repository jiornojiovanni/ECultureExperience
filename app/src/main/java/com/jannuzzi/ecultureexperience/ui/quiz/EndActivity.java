package com.jannuzzi.ecultureexperience.ui.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jannuzzi.ecultureexperience.R;

public class EndActivity extends AppCompatActivity {
    TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.results);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_result= findViewById(R.id.result);

        int correct= getIntent().getIntExtra("correct", 0);
        int wrong= getIntent().getIntExtra("wrong", 0);

        tv_result.setText(getString(R.string.answer, correct, wrong));

    }
}