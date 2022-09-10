package com.jannuzzi.ecultureexperience.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jannuzzi.ecultureexperience.MainActivity;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.data.LoginDataSource;
import com.jannuzzi.ecultureexperience.data.LoginRepository;
import com.jannuzzi.ecultureexperience.data.Result;
import com.jannuzzi.ecultureexperience.data.User;
import com.jannuzzi.ecultureexperience.data.model.LoggedInUser;
import com.jannuzzi.ecultureexperience.databinding.ActivityLoginBinding;
import com.jannuzzi.ecultureexperience.ui.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final TextView signup = binding.actionSignIn;
        loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    emailEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                    return;
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
                goToMain();

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    login(emailEditText.getText().toString(), passwordEditText.getText().toString());
                    //loginViewModel.login(emailEditText.getText().toString(),
                            //passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                login(emailEditText.getText().toString(), passwordEditText.getText().toString());
                //loginViewModel.login(emailEditText.getText().toString(),
                //      passwordEditText.getText().toString());
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                goToRegister();
            }
        });
    }

    public void onStart(){
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            //Complete and destroy login activity once successful
            finish();
            goToMain();
        }

    }
    private void goToMain() {
        Intent intent =new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goToRegister() {
        Intent intent =new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void login(String username, String password){
        FirebaseAuth mAuth;
        DatabaseReference reference;
        LoggedInUser realUser;
        String errorMessage="";
        Exception dbError;

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference reference = FirebaseDatabase.getInstance("https://e-cultureexperience-6a9da-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
                    String userID = user.getUid();

                    reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User userProfile = snapshot.getValue(User.class);
                            if(userProfile!= null){
                                LoggedInUser realUser =
                                        new LoggedInUser(userID, userProfile.name, userProfile.age, userProfile.lastName, userProfile.email);
                                LoginRepository.getInstance(new LoginDataSource()).login( new Result.Success<>(realUser));
                                finish();
                                goToMain();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loadingProgressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, R.string.Database_connection_error, Toast.LENGTH_LONG).show();
                            //errorMessage = "Problem contacting the database ";
                            //dbError = error.toException();

                        }
                    });
                }
                else {
                    loadingProgressBar.setVisibility(View.GONE);
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthInvalidUserException e) {
                        Toast.makeText(LoginActivity.this, R.string.invalid_credentials, Toast.LENGTH_LONG).show();
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(LoginActivity.this, R.string.invalid_login_password, Toast.LENGTH_LONG).show();
                    } catch(Exception e) {
                        Toast.makeText(LoginActivity.this, R.string.generic_login_error, Toast.LENGTH_LONG).show();

                    }
                }
            }
        });

    }
}