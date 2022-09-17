package com.jannuzzi.ecultureexperience.ui.qr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.ui.game.GameActivity;
import com.jannuzzi.ecultureexperience.ui.game.StartUp;
import com.jannuzzi.ecultureexperience.ui.quiz.Question;
import com.jannuzzi.ecultureexperience.ui.quiz.QuizActivity;
import com.jannuzzi.ecultureexperience.ui.rate.RateActivity;

public class QrScanner extends AppCompatActivity {
    Button btScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        btScan = findViewById(R.id.bt_scan);

        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(
                        QrScanner.this
                );

                intentIntegrator.setPrompt("For flash use volume up key");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(Capture.class);
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data
        );

        String content = intentResult.getContents();
        if (content != null) {
            if(content.equals("gioco")) {
                Intent intent = new Intent(this, StartUp.class);
                startActivity(intent);
            } else {
                Bundle qrData = new Bundle();
                qrData.putString("Name", content);
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtras(qrData);

                startActivity(intent);
            }


        } else {
            Toast.makeText(getApplicationContext(), "OOPS... You didn't scan anything", Toast.LENGTH_SHORT).show();

        }


    }

}