package com.jannuzzi.ecultureexperience.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.jannuzzi.ecultureexperience.MainActivity;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.data.UserRepository;
import com.jannuzzi.ecultureexperience.data.User;
import com.jannuzzi.ecultureexperience.databinding.ActivityLoginBinding;
import com.jannuzzi.ecultureexperience.ui.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final TextView signup = binding.actionSignIn;
        loadingProgressBar = binding.loading;

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!emailEditText.getText().toString().equals("") && !passwordEditText.getText().toString().equals("")) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    login(emailEditText.getText().toString(), passwordEditText.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), R.string.fill_fields, Toast.LENGTH_SHORT).show();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });
    }

    public void onStart() {
        super.onStart();
        if (UserRepository.getInstance().isLoggedIn()) {
            //Complete and destroy login activity once successful
            finish();
            goToMain();
        }

    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /*
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    } */

    private void login(String username, String password) {
        FirebaseAuth mAuth;
        DatabaseReference reference;
        User realUser;
        String errorMessage = "";
        Exception dbError;

        UserRepository.getInstance().login(username, password, new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                loadingProgressBar.setVisibility(View.GONE);
                if (msg.obj != null) {
                    Exception e = (Exception) msg.obj;
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(getApplicationContext(), R.string.invalid_credentials, Toast.LENGTH_LONG).show();
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(LoginActivity.this, R.string.invalid_login_password, Toast.LENGTH_LONG).show();
                    } else if(e instanceof FirebaseNetworkException) {
                        Toast.makeText(LoginActivity.this, R.string.not_connected, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.generic_login_error, Toast.LENGTH_LONG).show();
                    }
                } else {
                    finish();
                    goToMain();
                }
                return true;
            }
        });
    }
}