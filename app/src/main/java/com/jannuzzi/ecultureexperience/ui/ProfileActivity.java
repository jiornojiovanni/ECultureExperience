package com.jannuzzi.ecultureexperience.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jannuzzi.ecultureexperience.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).child("completedRoutes").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Long value = (Long) task.getResult().getValue();
                if(value != null) {
                    TextView routes = findViewById(R.id.paths_label);
                    routes.setText(value.toString());
                }
            }
        });
        reference.child(uid).child("completedGames").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Long value = (Long) task.getResult().getValue();
                if(value != null) {
                    TextView games = findViewById(R.id.games_label);
                    games.setText(value.toString());
                }
            }
        });
    }
}