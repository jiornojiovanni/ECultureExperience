package com.jannuzzi.ecultureexperience.ui.rate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.jannuzzi.ecultureexperience.MainActivity;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.ui.login.LoginActivity;
import com.jannuzzi.ecultureexperience.ui.register.RegisterActivity;

public class RateActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        final Button rate;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        rate =  findViewById(R.id.rate_confirm);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                goToMain();
                successRate();

            }
        });
    }

    private void goToMain() {
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void successRate() {
        String rated = getString(R.string.rate_shared);
        Toast.makeText(getApplicationContext(), rated, Toast.LENGTH_SHORT).show();
    }
}
