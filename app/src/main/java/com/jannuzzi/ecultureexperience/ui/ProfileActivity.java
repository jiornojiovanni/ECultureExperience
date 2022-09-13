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
import com.jannuzzi.ecultureexperience.data.LoginDataSource;
import com.jannuzzi.ecultureexperience.data.LoginRepository;
import com.jannuzzi.ecultureexperience.data.model.LoggedInUser;

public class ProfileActivity extends AppCompatActivity {

    private LoggedInUser temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        temp = LoginRepository.getInstance(new LoginDataSource()).getLoggedInUser();
        TextView nome = findViewById(R.id.profile_name);
        nome.setText(temp.getDisplayName());
        TextView cognome = findViewById(R.id.profile_lastname);
        cognome.setText(temp.getLastName());
        TextView email = findViewById(R.id.profile_email);
        email.setText(temp.getEmail());


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