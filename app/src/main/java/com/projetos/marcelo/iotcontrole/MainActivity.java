package com.projetos.marcelo.iotcontrole;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageButton;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageButton IbOnOff;
    Status status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IbOnOff = (ImageButton) findViewById(R.id.btOnOff);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
        status = Status.OFF;

        IbOnOff.setOnClickListener(new View.OnClickListener()
        {
            public void onClick (View v)
            {
                try {
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                    InetAddress serverEnd = InetAddress.getByName("192.168.0.146");
                    Socket socket = new Socket(serverEnd,27015);
                    Conector conector = new Conector();
                    Iot iot = new Iot();
                    conector.setIot(iot);
                    iot.setId("0");
                    iot.setName("ControlePilotoPradoVelho");
                    ButtonIot buttonIot = new ButtonIot();
                    buttonIot.setButtonID(1);
                    buttonIot.setStatus(status);
                    if(status.equals(Status.OFF))
                        status = Status.ON;
                    else
                        status = Status.OFF;
                    buttonIot.setTecla(Status.OUT);
                    List<ButtonIot> botoes = new ArrayList<ButtonIot>();
                    botoes.add(buttonIot);
                    iot.setjSon(gson.toJson(botoes));
                    conector.setId("0");
                    conector.setTipo(TipoIOT.HUMAN);
                    conector.setSenha("M@r0403");
                    conector.setUsuario("Matinhos");
                    conector.setStatus(Status.LOGINWITHCOMMAND);
                    String jSon = gson.toJson(conector);
                    PrintWriter out = new PrintWriter(
                            new BufferedWriter(new OutputStreamWriter(
                                    socket.getOutputStream())), true);
                    out.println(jSon);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    jSon = in.readLine();
                    conector = gson.fromJson(jSon, Conector.class);
                    Toast.makeText(getApplicationContext(), conector.getId(), Toast.LENGTH_LONG).show();
                    socket.close();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("NETWORK-RECEIVE", "Something goes wrong: IOException", e);
                }
            }
        });
    }
}