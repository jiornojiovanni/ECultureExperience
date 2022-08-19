package com.jannuzzi.ecultureexperience.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.jannuzzi.ecultureexperience.data.LoginRepository;
import com.jannuzzi.ecultureexperience.data.Result;
import com.jannuzzi.ecultureexperience.data.model.LoggedInUser;
import com.jannuzzi.ecultureexperience.R;
import com.jannuzzi.ecultureexperience.data.model.RegisterRepository;

public class RegisterViewModel extends ViewModel {
    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> loginResult = new MutableLiveData<>();
    private RegisterRepository registerRepository;

    RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    LiveData<RegisterFormState> getLoginFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        /*
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = registerRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
        */
    }

    public void loginDataChanged(String username, String password) {
        /*
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            registerFormState.setValue(new LoginFormState(true));
        }
        */
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
