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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView nome = findViewById(R.id.profile_name);
        TextView cognome = findViewById(R.id.profile_lastname);
        TextView email = findViewById(R.id.profile_email);
        TextView routes = findViewById(R.id.paths_label);
        TextView games = findViewById(R.id.games_label);

        UserRepository.getInstance().getCurrentUser(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                User user = (User) msg.obj;
                nome.setText(user.name);
                cognome.setText(user.lastName);
                email.setText(user.email);
                routes.setText(String.valueOf(user.completedRoutes));
                games.setText(String.valueOf(user.completedGames));
                return true;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}