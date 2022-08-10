package com.jannuzzi.ecultureexperience.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    TextView textView;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_percorsi);

        /*textView=(TextView)findViewById(R.id.login);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });*/
    }


}
