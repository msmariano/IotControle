package com.projetos.marcelo.iotcontrole.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.projetos.marcelo.iotcontrole.ClienteMQTT;
import com.projetos.marcelo.iotcontrole.Conector;
import com.projetos.marcelo.iotcontrole.Conexao;
import com.projetos.marcelo.iotcontrole.Login;
import com.projetos.marcelo.iotcontrole.MainActivity;


import com.projetos.marcelo.iotcontrole.R;
import com.projetos.marcelo.iotcontrole.SettingsActivity;
import com.projetos.marcelo.iotcontrole.Status;
import com.projetos.marcelo.iotcontrole.TipoIOT;
import com.projetos.marcelo.iotcontrole.data.Result;
import com.projetos.marcelo.iotcontrole.data.model.LoggedInUser;
import com.projetos.marcelo.iotcontrole.ui.login.LoginViewModel;
import com.projetos.marcelo.iotcontrole.ui.login.LoginViewModelFactory;
import com.projetos.marcelo.iotcontrole.databinding.ActivityLoginBinding;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity implements IMqttMessageListener {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private LoginActivity la;

    private String idGerado = "";

    private final Object obj = new Object();

    private String user = "";

    private Boolean logando = false;

    private String userBio;

    private String passwBio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        la = this;
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

            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                logar(userBio,passwBio);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

            }
        });

        //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        //startActivity(intent);

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Neuverse Controle IOT")
                .setSubtitle("Desbloqueio seu app")
                .setNegativeButtonText("Usar Pin")
                .build();

        //biometricPrompt.authenticate(promptInfo);

        String jSon = lerCfgServidor();
        if(!jSon.trim().isEmpty()) {
            try {
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                Login login = gson.fromJson(jSon, Login.class);
                if (login.getResultado()) {
                    userBio = login.getUser();
                    passwBio = login.getPass();
                    biometricPrompt.authenticate(promptInfo);
                    salvarCfgServidor("");
                    loginButton.setVisibility(View.INVISIBLE);
                    usernameEditText.setText(userBio);
                    passwordEditText.setText(passwBio);
                }
                else
                    jSon = "";
            }
            catch (Exception e){
                jSon = "";
            }
        }
        if(jSon.trim().isEmpty()) {

            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            //startActivity(intent);

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
        }

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
                Toast.makeText(la,"Logando",Toast.LENGTH_LONG).show();
                logar( usernameEditText.getText().toString(),passwordEditText.getText().toString());


            }
        });
    }


    void logar(String userName, String password){
        if(!logando) {
            logando = true;
            try {

                UUID uniqueKey = UUID.randomUUID();
                idGerado = userName + "_" + uniqueKey.toString();
                Login login = new Login();
                login.setUser(userName);
                user = userName;
                login.setPass(password);
                login.setUuid(idGerado);
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                String jSon = gson.toJson(login);
                ClienteMQTT clienteMQTT = new ClienteMQTT("ssl://f897f821.ala.us-east-1.emqxsl.com:8883", "neuverse", "M@r040370");
                clienteMQTT.mqttOptions.setSSLHostnameVerifier(null);
                clienteMQTT.iniciar(userName);
                clienteMQTT.subscribe(0, la, "br/com/neuverse/login/" + idGerado + "/retorno");
                clienteMQTT.publicar("br/com/neuverse/geral/login/login_super_neuverse_Z3li@040370_1635_36f0ee17-b370-4635-96ba-d6f1b3b6ad82/validar", jSon.getBytes(), 1);
                synchronized (obj) {
                    obj.wait(5000);
                }

                clienteMQTT.getClient().disconnect();
                clienteMQTT.getClient().close();

            } catch (Exception e) {
                Toast.makeText(la,"Erro ao enviar login mqtt:"+e.getMessage(),Toast.LENGTH_LONG).show();
            }
            logando = false;
        }
    }


    public String lerCfgServidor() {
        String fileName = "Cfg";
        FileInputStream inputStream = null;
        String s = "";
        try {
            inputStream = openFileInput(fileName);

            int i = inputStream.read();
            if (i != -1)
                s = s + (char) i;
            while (i != -1) {
                i = inputStream.read();
                if (i != -1)
                    s = s + (char) i;
            }
        } catch (Exception e) {
        }
        return s;
    }

    public void salvarCfgServidor(String endServidor) {
        String fileName = "Cfg";
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(endServidor.getBytes());
            outputStream.close();
        } catch (Exception e) {
        }
    }

    private void updateUiWithUser(LoggedInUserView model) {
        //String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        //Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if(topic.equals("br/com/neuverse/login/"+idGerado+"/retorno")) {
            try {
                synchronized (obj) {
                    obj.notifyAll();
                }
                String uuidCliente = "";
                String jSon = new String(message.getPayload());
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                Login login =  gson.fromJson(jSon,Login.class);
                uuidCliente = login.getUuid();
                if(login.getResultado()){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    salvarCfgServidor(jSon);
                    String sExtra[] =  login.getUuidIOTs().toArray(new String[0]);
                    intent.putExtra("uuids",sExtra);
                    intent.putExtra("idGerado",idGerado);
                    intent.putExtra("user",user);
                    startActivity(intent);
                }
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
    }
}