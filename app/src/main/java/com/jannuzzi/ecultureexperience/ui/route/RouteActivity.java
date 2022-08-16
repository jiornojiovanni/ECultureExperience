package com.jannuzzi.ecultureexperience.ui.route;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.content.ClipData;
import android.os.Bundle;
import com.jannuzzi.ecultureexperience.R;

public class RouteActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_path);

        final CardView cardView = findViewById(R.id.card_event);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                cardView.setChecked(!cardView.isChecked());
                cardView.toggle();
            }
        });
    }


}