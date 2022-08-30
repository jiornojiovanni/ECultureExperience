package com.jannuzzi.ecultureexperience.ui.route;

import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        setContentView(R.layout.activity_route);

        String pathFile = getIntent().getExtras().getString("pathFile");
        InputStream fileContent = readPathFile(pathFile);
        if(fileContent != null) {
            List<Route> instructions = parsePathFile(fileContent);
            displayInstructions(instructions);
        } else {
            Toast.makeText(this, R.string.route_error, Toast.LENGTH_LONG).show();
        }
    }

    private void displayInstructions(List<Route> instructions) {
        LinearLayout layout = findViewById(R.id.routeLayout);
        for (Route route:
                instructions) {
            LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.row_path, null);
            MaterialCardView view = row.findViewById(R.id.card_event);
            view.setOnClickListener(clicked -> {
                MaterialCardView card = (MaterialCardView) clicked;
                card.setChecked(!card.isChecked());
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
}