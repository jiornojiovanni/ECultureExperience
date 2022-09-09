package com.jannuzzi.ecultureexperience.data;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jannuzzi.ecultureexperience.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseUser user;
    private String userID;
    private LoggedInUser realUser;
    private String errorMessage="";
    private Exception dbError;

    public Result<LoggedInUser> login(String username, String password) {

        try {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        reference = FirebaseDatabase.getInstance("https://e-cultureexperience-6a9da-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
                        userID = user.getUid();

                        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User userProfile = snapshot.getValue(User.class);
                                if(userProfile!= null){
                                    realUser =
                                            new LoggedInUser( userID, userProfile.name, userProfile.age, userProfile.lastName, userProfile.email);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                errorMessage = "Problem contacting the database ";
                                dbError = error.toException();

                            }
                        });
                    }
                    else {
                        if((task.getException() == null ) ? false : true )
                            errorMessage = "Problem contacting the database ";
                        dbError = task.getException();
                    }
                }
            });

            if(errorMessage.isEmpty()==false){
                return new Result.Error(new IOException("Error logging in", dbError));

            }
            return new Result.Success<>(realUser);
/*
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);

 */
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }

}