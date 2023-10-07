package com.projetos.marcelo.iotcontrole;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.projetos.marcelo.iotcontrole.ui.login.LoginActivity;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements IMqttMessageListener {

    SQLiteDatabase mydatabase;
    AppCompatActivity activity;
    ListView simpleList;
    List<Dispositivo> dispositivos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Controle");
        activity = this;
        FloatingActionButton btCfg = findViewById(R.id.bCfg);
        btCfg.setOnClickListener(view -> {
            //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            //startActivity(intent);
            salvarCfgServidor("");
            Toast.makeText(getApplicationContext(), "Configuração apagada!", Toast.LENGTH_LONG).show();

        });
        //Configuracao.deleteAll(Configuracao.class);

        mydatabase = openOrCreateDatabase("cfg.db", MODE_PRIVATE, null);
        //mydatabase.execSQL("DROP tABLE IF EXISTS PARAMETRO");
        //mydatabase.execSQL("DROP TABLE IF EXISTS CONFIGURACAO");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Configuracao(ID INTEGER,SERVIDOR VARCHAR," +
                "PORTASERVIDOR INTEGER,USUARIO VARCHAR,SENHA VARCHAR,NOMEIOTCOM VARCHAR," +
                "NOMEIOT VARCHAR, NOMEBOTAO VARCHAR,IDBOTAO INTEGER );");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS PARAMETRO(ID INTEGER,PARAMETRO VARCHAR,CAMPO1 VARCHAR,CAMPO2 VARCHAR);");
    }

    @Override
    public void onResume() {
        super.onResume();
        try {

            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
            simpleList = (ListView) findViewById(R.id.simpleListView);

            ClienteMQTT clienteMQTT = new ClienteMQTT("tcp://broker.mqttdashboard.com:1883", "neuverse", "M@r040370");
            clienteMQTT.iniciar();
            clienteMQTT.subscribe(0, this, "br/com/neuverse/servidores/events/#");
            clienteMQTT.subscribe(0, this, "br/com/neuverse/servidores/lista");
            Thread.sleep(1000);
            clienteMQTT.publicar("br/com/neuverse/geral/info",  "Cliente".getBytes(), 1);


            /*String ret = "";
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                        .hasMoreElements();) {
                    NetworkInterface ni = en.nextElement();
                    for (Enumeration<InetAddress> enumIP = ni.getInetAddresses(); enumIP.hasMoreElements();) {
                        InetAddress ip = enumIP.nextElement();
                        if (!ip.isLoopbackAddress()) {
                            String s = ip.getHostAddress();
                            if(s.contains(".")) {
                                ret += " " + ip.getHostAddress();
                            }
                        }
                    }
                }
            } catch (SocketException e) {

            }

            Rest rest = new Rest();
            rest.setPorta("27016");


            if(lerCfgServidor().trim().equals("")){
                boolean encontrado = false;
                try {
                    String ipEncontrados[] = ret.trim().split(" ");
                    for (String ipE : ipEncontrados) {
                        ipE = ipE.trim();
                        ipE = ipE.replace(".",";");
                        String ip[] = new String[4];
                        String ipComposto[] = ipE.split(";");
                        Integer octfinal = Integer.valueOf(ipComposto[3]);

                        for (int i = 1; i < 255; i++) {
                            if (octfinal != i) {
                                String ipTeste = ipComposto[0] + "." + ipComposto[1] + "." + ipComposto[2] + "." + i;
                                rest.setIp(ipTeste);
                                rest.setUri("/ServidorIOT/info");
                                try {
                                    String jSon = rest.sendRest("");
                                    if (!jSon.equals("")) {
                                        Toast.makeText(getApplicationContext(), "ServidorIOT encontrado, gravando configuração!", Toast.LENGTH_LONG).show();
                                        ret = ipTeste+";"+lerCfgServidor();
                                        encontrado = true;
                                        salvarCfgServidor(ipTeste);
                                        break;
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                }
                catch(Exception e){
                }
            }

            ret = lerCfgServidor();

            acrescentarServidorIOT(ret);*/


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (topic.equals("br/com/neuverse/servidores/lista")) {
            acrescentarServidorIOT(new String(message.getPayload()));
        }
    }

    public void acrescentarServidorIOT(String jSon){
        try {
            /*Rest rest = new Rest();
            rest.setPorta("27016");
            rest.setIp(ip);
            rest.setUri("/ServidorIOT/listar");

            String jSon = rest.sendRest("");*/
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();

            Type listType = new TypeToken<ArrayList<Pool>>() {
            }.getType();
            List<Pool> pools = gson.fromJson(jSon, listType);
            for (Pool pool : pools) {
                for (Dispositivo dispositivo : pool.getDispositivos()) {
                    dispositivo.setIdpool(pool.getId());
                    dispositivo.setEndServidor("");
                }
                dispositivos.addAll(pool.getDispositivos());
            }

            CustomAdapter arrayAdapter = new CustomAdapter(this, dispositivos);
            simpleList.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String lerCfgServidor(){
        String fileName = "Cfg";
        FileInputStream inputStream = null;
        String s="";
        try {
            inputStream = openFileInput(fileName);

            int i = inputStream.read();
            if(i != -1)
                s = s+(char)i;
            while( i != -1){
                i = inputStream.read();
                if(i != -1)
                    s = s+(char)i;
            }
        } catch (Exception e) {
        }
        return s;
    }
    public void salvarCfgServidor(String endServidor){
        String fileName = "Cfg";
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(endServidor.getBytes());
            outputStream.close();
        } catch (Exception e) {;
        }
    }

}