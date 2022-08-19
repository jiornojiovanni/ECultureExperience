package com.jannuzzi.ecultureexperience.ui.register;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.data.User;
import com.jannuzzi.ecultureexperience.databinding.ActivityLoginBinding;
import com.jannuzzi.ecultureexperience.databinding.ActivityRegisterBinding;
import com.jannuzzi.ecultureexperience.ui.login.LoginActivity;
import com.jannuzzi.ecultureexperience.ui.login.LoginFormState;
import com.jannuzzi.ecultureexperience.ui.login.LoginViewModel;
import com.jannuzzi.ecultureexperience.ui.login.LoginViewModelFactory;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private RegisterViewModel registerViewModel;
    private ActivityRegisterBinding binding;

    /*
    private TextView textView;
    private Button registerBtn;
    private EditText editEmail,  editName, editLastName, editPassword, editAge;
    // private ProgressBar; //used to show the classic loading feedback
    */
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_register);

        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory())
                .get(LoginViewModel.class);


        mAuth = FirebaseAuth.getInstance();

        final EditText name = binding.name;
        final EditText lastName = binding.Lastname;
        final EditText age = binding.age;

        final EditText editEmail = binding.emailReg;
        final EditText editPassword = binding.passwordReg;
        final Button registerBtn = binding.register;
        final TextView login = binding.login;

        registerViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
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


        //add loading registration bar
        //final ProgressBar registrationProgressBar = binding.loading;

/*
        textView=(TextView)findViewById(R.id.login);
        textView.setOnClickListener(this);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        registerBtn = findViewById(R.id.register);
        registerBtn.setOnClickListener(this);



        editEmail =(EditText)findViewById(R.id.email_reg);
        editName=(EditText)findViewById(R.id.name);
        editLastName=(EditText)findViewById(R.id.Lastname);
        editPassword =(EditText)findViewById(R.id.password_reg);
        editAge=(EditText)findViewById(R.id.age);
 */


    }

    @Override
    public void onClick(View v) {
        //if is used instead of switch since id are not final
        if(v.getId()==R.id.login) {
            goToLogin();
        }
        else if(v.getId()==R.id.register){
                registerUser();
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


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name, lastName, age, email);
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "User has been registered succesfully", Toast.LENGTH_LONG).show();

                                                //redirect to login
                                            }else{
                                                Toast.makeText(RegisterActivity.this, "Failed to register user", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(RegisterActivity.this, "Failed to register user", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
