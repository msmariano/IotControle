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
        IbOnOff =  findViewById(R.id.btOnOff);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
        status = Status.OFF;
        envia(gerarConectorJson(Status.LOGINWITHCOMMAND,true));

        IbOnOff.setOnClickListener(new View.OnClickListener()
        {
            public void onClick (View v)
            {
                envia(gerarConectorJson(Status.LOGINWITHCOMMAND,false));
            }
        });
    }

    public String gerarBotoesJson(boolean bRead){
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        ButtonIot buttonIot = new ButtonIot();
        buttonIot.setButtonID(1);

        if(!bRead) {
            buttonIot.setStatus(Status.OUT);
            if (status.equals(Status.OFF))
                status = Status.ON;
            else
                status = Status.OFF;
            buttonIot.setTecla(status);
        }
        else{
            buttonIot.setStatus(Status.READ);
            buttonIot.setTecla(Status.NA);
        }

        List<ButtonIot> botoes = new ArrayList<>();
        botoes.add(buttonIot);
        return gson.toJson(botoes);
    }

    public Iot gerarIot(boolean bRead){
        Iot iot = new Iot();
        iot.setId("0");
        iot.setName("ControlePilotoPradoVelho");
        iot.setjSon(gerarBotoesJson(bRead));
        return iot;
    }

    public String gerarConectorJson(Status sEnvio,boolean bRead){
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        Conector conector = new Conector();
        conector.setIot(gerarIot(bRead));
        conector.setId("0");
        conector.setTipo(TipoIOT.HUMAN);
        conector.setNome("ControleCelularMarcelo");
        conector.setSenha("M@r0403");
        conector.setUsuario("Matinhos");
        conector.setStatus(sEnvio);
       return gson.toJson(conector);
    }

    public void trataRetorno(String jSonRetorno){
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        Conector conector = gson.fromJson(jSonRetorno, Conector.class);
        if(conector.getStatus().equals(Status.RETORNO)) {
            if(conector.getIot().getjSon()!=null){
                ButtonIot buttonIot = gson.fromJson(conector.getIot().getjSon(),ButtonIot.class);
                status = buttonIot.getStatus();
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(getApplicationContext(), conector.getIot().getjSon(), Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), conector.getId(), Toast.LENGTH_LONG).show();
        }
    }

    public void envia(String textJson){
        try {
            InetAddress serverEnd = InetAddress.getByName("192.168.0.146");
            Socket socket = new Socket(serverEnd,27015);
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream())), true);
            out.println(textJson);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            trataRetorno(in.readLine());
            socket.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("NETWORK-RECEIVE", "Something goes wrong: IOException", e);
        }
    }
}