package com.jannuzzi.ecultureexperience.ui.route;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.data.JSONParser;
import com.jannuzzi.ecultureexperience.data.User;
import com.jannuzzi.ecultureexperience.data.UserRepository;
import com.jannuzzi.ecultureexperience.data.model.Route;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity {

    private List<Route> instructions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.route);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String pathFile = getIntent().getExtras().getString("pathFile");

        instructions = restoreState(pathFile);
        if(instructions == null) {
            InputStream fileContent = JSONParser.readFile("Download", pathFile);
            if(fileContent != null) {
                instructions = JSONParser.parseRoute(fileContent);
                displayInstructions(instructions);
            } else {
                Toast.makeText(this, R.string.route_error, Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            displayInstructions(instructions);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveState(instructions);
    }

    private void displayInstructions(List<Route> instructions) {
        LinearLayout layout = findViewById(R.id.routeLayout);
        for (Route route:
                instructions) {
            LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.row_path, null);
            MaterialCardView view = row.findViewById(R.id.card_event);
            if(route.getChecked()) {
                view.setChecked(true);
            }
            view.setOnClickListener(clicked -> {
                MaterialCardView card = (MaterialCardView) clicked;
                card.setChecked(!card.isChecked());
                route.flip();
                saveState(instructions);
                //the user has completed the route;
                if(checkIfCompleted(instructions)) {
                    updateBadgeCount();
                    Toast.makeText(getApplicationContext(), R.string.completed_route, Toast.LENGTH_LONG).show();
                }
            });
            ((TextView) view.findViewById(R.id.title)).setText(route.getTitle());
            ((TextView) view.findViewById(R.id.description)).setText(route.getDescription());

            layout.addView(row);
        }
    }

    private void saveState(List<Route> instructions) {
        try {
            FileOutputStream stream = getApplicationContext().openFileOutput(getIntent().getExtras().getString("pathFile"), Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(instructions);
            out.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Route> restoreState(String pathFile) {
        try {
            FileInputStream stream = getApplicationContext().openFileInput(pathFile);
            ObjectInputStream in = new ObjectInputStream(stream);
            List<Route> list = (List<Route>) in.readObject();
            in.close();
            stream.close();
            return list;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateBadgeCount() {

        UserRepository.getInstance().getCurrentUser(msg -> {
            User user = (User) msg.obj;
            user.completedRoutes++;
            UserRepository.getInstance().updateCurrentUser(user);
            return true;
        });
    }

    private boolean checkIfCompleted(List<Route> instructions) {
        for (Route instruction:
             instructions) {
            if (!instruction.getChecked()) {
                return false;
            }
        }
        return true;
    }
}