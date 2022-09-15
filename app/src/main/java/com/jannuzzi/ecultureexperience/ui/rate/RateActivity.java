package com.jannuzzi.ecultureexperience.ui.rate;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jannuzzi.ecultureexperience.R;

public class RateActivity extends AppCompatActivity {

    TextView cardTitle, tvCardSecond;
    RatingBar bar;
    Button rate;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.rate);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        cardTitle = findViewById(R.id.cardTitle);
        tvCardSecond = findViewById(R.id.tvCardSecond);
        bar = findViewById(R.id.smallRatingBar);
        cardTitle.setText(getIntent().getExtras().getString("name"));
        tvCardSecond.setText(getIntent().getExtras().getString("description"));
        String imgPath = getIntent().getExtras().getString("imgPath");

        StorageReference imgReference = FirebaseStorage.getInstance().getReference().child(imgPath);
        imgReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            ((ShapeableImageView) findViewById(R.id.ivCard)).setImageBitmap(bmp);
        });

        bar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
           rate.setEnabled(true);
        });

        rate =  findViewById(R.id.rate_confirm);
        rate.setOnClickListener(view -> {
            successRate();

            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void successRate() {
        Toast.makeText(getApplicationContext(), R.string.rate_shared, Toast.LENGTH_SHORT).show();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_visited) + " " + cardTitle.getText() + "\n" +getString(R.string.review) + bar.getRating() + " ⭐" );
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with)));
    }
}



