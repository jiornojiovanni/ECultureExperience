package com.jannuzzi.ecultureexperience.ui.register;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.data.User;
import com.jannuzzi.ecultureexperience.ui.login.LoginActivity;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private ScrollView View;
    private TextView login;
    private Button registerBtn;
    private EditText editEmail, editName, editLastName, editPassword, editAge;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        login = (TextView) findViewById(R.id.login);
        login.setOnClickListener(this);

        registerBtn = findViewById(R.id.register);
        registerBtn.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.email_reg);
        editName = (EditText) findViewById(R.id.name);
        editLastName = (EditText) findViewById(R.id.Lastname);
        editPassword = (EditText) findViewById(R.id.password_reg);
        editAge = (EditText) findViewById(R.id.age);

    }

    @Override
    public void onClick(View v) {
        //if is used instead of switch since id are not final
        if (v.getId() == R.id.login) {
            finish();
            goToLogin();
        } else if (v.getId() == R.id.register) {
            if (editPassword.getText().toString().length() < 8) {
                Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches()) {
                Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_SHORT).show();
            } else if (editName.getText().toString().equals("")
                    || editLastName.getText().toString().equals("")
                    || editAge.getText().toString().equals("")
                    || editPassword.getText().toString().equals("")) {
                Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_SHORT).show();
            } else {
                registerUser();
            }
        }
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void registerUser() {

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String name = editName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String age = editAge.getText().toString().trim();

        //set language variable later
        //reverse order so it focuses the ones on top last, probably can be improved
        //password must be 6 characters long

        //add try and catch for firebase errors
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = new User(name, lastName, age, email);
                        addOnDatabase(user);
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), R.string.email_alreadyused, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void addOnDatabase(User user) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference("Users");
        fb.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), R.string.successfull_register, Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        finish();
                        goToLogin();
                        //redirect to login
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), R.string.failed_register, Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }
}

