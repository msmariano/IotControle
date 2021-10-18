package com.projetos.marcelo.iotcontrole.ui.login;

import android.app.Activity;
import android.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import androidx.biometric.BiometricPrompt;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

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

import com.projetos.marcelo.iotcontrole.Conector;
import com.projetos.marcelo.iotcontrole.Conexao;
import com.projetos.marcelo.iotcontrole.MainActivity;


import com.projetos.marcelo.iotcontrole.SettingsActivity;
import com.projetos.marcelo.iotcontrole.Status;
import com.projetos.marcelo.iotcontrole.TipoIOT;
import com.projetos.marcelo.iotcontrole.data.Result;
import com.projetos.marcelo.iotcontrole.data.model.LoggedInUser;
import com.projetos.marcelo.iotcontrole.ui.login.LoginViewModel;
import com.projetos.marcelo.iotcontrole.ui.login.LoginViewModelFactory;
import com.projetos.marcelo.iotcontrole.databinding.ActivityLoginBinding;

import java.io.IOException;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

            }
        });

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Neuverse Controle IOT")
                .setSubtitle("Desbloqueio seu app")
                .setNegativeButtonText("Usar Pin")
                .build();

        biometricPrompt.authenticate(promptInfo);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
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
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
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
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*loadingProgressBar.setVisibility(View.VISIBLE);
                // can be launched in a separate asynchronous job
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Conector conector = new Conector();
                        conector.setUsuario(usernameEditText.getText().toString());
                        conector.setSenha(passwordEditText.getText().toString());
                        conector.setStatus(Status.LOGIN);
                        conector.setTipo(TipoIOT.HUMAN);
                        Conexao conexao = new Conexao();
                        conexao.setServidor("192.168.0.116");
                        conexao.setPorta(27015);
                        conexao.envia(conector);
                        if(conexao.getListaConector().size()>0){
                            if(conexao.getListaConector().get(0).getStatus().equals(Status.LOGIN_OK)){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                            }
                        }
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                });

                t.start();

                while(t.isAlive());*/

                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //startActivity(intent);


            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        //String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        //Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}