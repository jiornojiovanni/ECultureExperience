package com.jannuzzi.ecultureexperience.data;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class UserRepository {

    private static volatile UserRepository instance;

    // private constructor : singleton access
    private UserRepository() {
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return false;
        } else {
            return true;
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public void login(String username, String password, Handler.Callback callback) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    callback.handleMessage(Message.obtain());
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Message errorMsg = Message.obtain();
                        errorMsg.obj = e;
                        callback.handleMessage(errorMsg);
                    }
                }
            }
        });
    }

    public void getCurrentUser(Handler.Callback callback) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Message msg = Message.obtain();
                        msg.obj = task.getResult().getValue(User.class);
                        callback.handleMessage(msg);
                    }
                });

    }

    public void updateCurrentUser(User user) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(user, (error, ref) -> {

        });

    }
}