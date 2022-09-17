package com.jannuzzi.ecultureexperience.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.data.UserRepository;
import com.jannuzzi.ecultureexperience.data.User;

public class ProfileActivity extends AppCompatActivity {

    TextView nome, cognome, email, routes, games;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserRepository.getInstance().getCurrentUser(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                setupUI();

                User user = (User) msg.obj;
                nome.setText(user.name);
                cognome.setText(user.lastName);
                email.setText(user.email);
                routes.setText(String.valueOf(user.completedRoutes));
                games.setText(String.valueOf(user.completedGames));

                return true;
            }
        });
    }

    private void setupUI() {
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nome = findViewById(R.id.profile_name);
        cognome = findViewById(R.id.profile_lastname);
        email = findViewById(R.id.profile_email);
        routes = findViewById(R.id.paths_label);
        games = findViewById(R.id.games_label);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}