package com.jannuzzi.ecultureexperience.ui.route;

import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.data.model.Route;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_path);

        String pathFile = getIntent().getExtras().getString("pathFile");
        InputStream fileContent = readPathFile(pathFile);
        List<Route> instructions = parsePathFile(fileContent);
        Log.w("path", instructions.toString());

        MaterialCardView view = findViewById(R.id.card_event);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialCardView card = (MaterialCardView) view;
                card.setChecked(!card.isChecked());
            }
        });
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
}