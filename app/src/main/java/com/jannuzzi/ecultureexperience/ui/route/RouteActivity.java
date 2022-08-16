package com.jannuzzi.ecultureexperience.ui.route;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.jannuzzi.ecultureexperience.R;

public class RouteActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_path);

        MaterialCardView view = findViewById(R.id.card_event);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialCardView card = (MaterialCardView) view;
                card.setChecked(!card.isChecked());
            }
        });
    }


}