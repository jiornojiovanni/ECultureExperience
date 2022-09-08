package com.jannuzzi.ecultureexperience.ui.register;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.data.User;
import com.jannuzzi.ecultureexperience.databinding.ActivityRegisterBinding;
import com.jannuzzi.ecultureexperience.ui.login.LoginActivity;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    //private RegisterViewModel registerViewModel;
    //private ActivityRegisterBinding binding;
    private ScrollView View;
    private TextView login;
    private Button registerBtn;
    private EditText editEmail,  editName, editLastName, editPassword, editAge;

    // private ProgressBar; //used to show the classic loading feedback

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //View = (ScrollView) findViewById(R.id.scrollView4);

        mAuth = FirebaseAuth.getInstance();
/*
        final EditText name = binding.name;
        final EditText lastName = binding.Lastname;
        final EditText age = binding.age;
        final EditText editEmail = binding.emailReg;
        final EditText editPassword = binding.passwordReg;
        //final Button registerBtn = binding.register;
        //final TextView login = binding.login;
*/


        //add loading registration bar
        //final ProgressBar registrationProgressBar = binding.loading;

        login=(TextView)findViewById(R.id.login);
        login.setOnClickListener(this);

        /*
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        */

        registerBtn = findViewById(R.id.register);
        registerBtn.setOnClickListener(this);

        editEmail =(EditText)findViewById(R.id.email_reg);
        editName=(EditText)findViewById(R.id.name);
        editLastName=(EditText)findViewById(R.id.Lastname);
        editPassword =(EditText)findViewById(R.id.password_reg);
        editAge=(EditText)findViewById(R.id.age);

    }

    @Override
    public void onClick(View v) {
        //if is used instead of switch since id are not final
        if(v.getId()==R.id.login) {
            finish();
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

        //add try and catch for firebase errors
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task1) {
                        if(task1.isSuccessful()){
                            User user = new User(name, lastName, age, email);
                              DatabaseReference fb=FirebaseDatabase.getInstance("https://e-cultureexperience-6a9da-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
                                    Log.w("istanza", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    //temp2 = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    fb.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task2) {
                                            if(task2.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "User has been registered succesfully", Toast.LENGTH_LONG).show();
                                                mAuth.signOut();
                                                finish();
                                                goToLogin();
                                                //redirect to login
                                            }else{
                                                try {
                                                    throw task2.getException();
                                                } catch(Exception e) {
                                                    Log.e("eccezioneFB", e.getMessage());
                                                }
                                                Toast.makeText(RegisterActivity.this, "Failed to register user", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });
                        }else {
                            Log.w("eccezione", task1.getException());
                            Exception temp = task1.getException();
                            Toast.makeText(RegisterActivity.this, "Failed to register user, Email already in use", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

