package com.projetos.marcelo.iotcontrole.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Intent;
import android.util.Patterns;
import android.view.View;

import com.projetos.marcelo.iotcontrole.Conector;
import com.projetos.marcelo.iotcontrole.Conexao;
import com.projetos.marcelo.iotcontrole.MainActivity;
import com.projetos.marcelo.iotcontrole.Status;
import com.projetos.marcelo.iotcontrole.TipoIOT;
import com.projetos.marcelo.iotcontrole.data.LoginRepository;
import com.projetos.marcelo.iotcontrole.data.Result;
import com.projetos.marcelo.iotcontrole.data.model.LoggedInUser;
import com.projetos.marcelo.iotcontrole.R;

import java.io.IOException;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    Result<LoggedInUser> result;

    public void login(String username, String password) {

    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
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