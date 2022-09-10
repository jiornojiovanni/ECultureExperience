package com.jannuzzi.ecultureexperience.ui.route;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.data.model.Route;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
            InputStream fileContent = readPathFile(pathFile);
            if(fileContent != null) {
                instructions = parsePathFile(fileContent);
                displayInstructions(instructions);
            } else {
                Toast.makeText(this, R.string.route_error, Toast.LENGTH_LONG).show();
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
            });
            ((TextView) view.findViewById(R.id.title)).setText(route.getTitle());
            ((TextView) view.findViewById(R.id.description)).setText(route.getDescription());

            layout.addView(row);
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

    private List<Route> parsePathFile(InputStream content) {
        JsonReader reader = new JsonReader(new InputStreamReader(content));
        List<Route> instructions = new ArrayList<>();
        try {
            reader.beginArray();

            while (reader.hasNext()) {
                String title = "";
                String description = "";

                reader.beginObject();
                while (reader.hasNext()) {
                    String token = reader.nextName();
                    if ("title".equals(token)) {
                        title = reader.nextString();
                    } else if ("description".equals(token)) {
                        description = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                }

                reader.endObject();
                instructions.add(new Route(title, description));
            }

            return instructions;
        } catch (IOException e) {
            e.printStackTrace();
            return instructions;
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
}