package com.jannuzzi.ecultureexperience.ui.rate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.jannuzzi.ecultureexperience.MainActivity;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.ui.login.LoginActivity;
import com.jannuzzi.ecultureexperience.ui.register.RegisterActivity;

public class RateActivity extends AppCompatActivity {

    TextView cardTitle, tvCardSecond;
    RatingBar bar;
    Button rate;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        cardTitle = findViewById(R.id.cardTitle);
        tvCardSecond = findViewById(R.id.tvCardSecond);
        bar = findViewById(R.id.smallRatingBar);

        cardTitle.setText(getIntent().getExtras().getString("name"));
        tvCardSecond.setText(getIntent().getExtras().getString("description"));

        bar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
           rate.setEnabled(true);
        });

        rate =  findViewById(R.id.rate_confirm);
        rate.setOnClickListener(view -> {
            successRate();
            finish();
        });
    }

    private void successRate() {
        Toast.makeText(getApplicationContext(), R.string.rate_shared, Toast.LENGTH_SHORT).show();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_visited) + " " + cardTitle.getText() + "\n" +getString(R.string.review) + bar.getRating() + " ⭐" );
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with)));
    }
}
