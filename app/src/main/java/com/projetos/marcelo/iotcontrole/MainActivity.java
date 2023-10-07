package com.projetos.marcelo.iotcontrole;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements IMqttMessageListener {

    SQLiteDatabase mydatabase;
    AppCompatActivity activity;
    ListView simpleList;
    List<Dispositivo> dispositivos = new ArrayList<>();

    ClienteMQTT clienteMQTT;
    boolean inserido = false;

    boolean inicializado = false;

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
            //clienteMQTT.publicar("br/com/neuverse/geral/info", "Cliente".getBytes(), 1);
            //Toast.makeText(getApplicationContext(), "Publicado pedido de info!", Toast.LENGTH_LONG).show();

        });
        //Configuracao.deleteAll(Configuracao.class);

        mydatabase = openOrCreateDatabase("cfg.db", MODE_PRIVATE, null);
        //mydatabase.execSQL("DROP tABLE IF EXISTS PARAMETRO");
        //mydatabase.execSQL("DROP TABLE IF EXISTS CONFIGURACAO");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Configuracao(ID INTEGER,SERVIDOR VARCHAR," +
                "PORTASERVIDOR INTEGER,USUARIO VARCHAR,SENHA VARCHAR,NOMEIOTCOM VARCHAR," +
                "NOMEIOT VARCHAR, NOMEBOTAO VARCHAR,IDBOTAO INTEGER );");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS PARAMETRO(ID INTEGER,PARAMETRO VARCHAR,CAMPO1 VARCHAR,CAMPO2 VARCHAR);");

        try {
            if(!inicializado) {
                inicializado = true;

                StrictMode.ThreadPolicy gfgPolicy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(gfgPolicy);
                clienteMQTT = new ClienteMQTT("tcp://broker.mqttdashboard.com:1883", "neuverse", "M@r040370");
                clienteMQTT.iniciar();
                clienteMQTT.subscribe(0, this, "br/com/neuverse/servidores/events/#");
                clienteMQTT.subscribe(0, this, "br/com/neuverse/servidores/lista");
                //clienteMQTT.publicar("br/com/neuverse/geral/info", "Cliente".getBytes(), 1);
                simpleList = (ListView) findViewById(R.id.simpleListView);
                clienteMQTT.publicar("br/com/neuverse/geral/info", "Cliente".getBytes(), 1);
                //Toast.makeText(getApplicationContext(), "Inicializado", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println(new String(message.getPayload()));
        if (topic.equals("br/com/neuverse/servidores/lista")) {

                acrescentarServidorIOT(new String(message.getPayload()));

        }
    }

    public void acrescentarServidorIOT(String jSon){
        try {

            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();

            Type listType = new TypeToken<ArrayList<Pool>>() {
            }.getType();
            List<Pool> pools = gson.fromJson(jSon, listType);
            for (Pool pool : pools) {
                for (Dispositivo dispositivo : pool.getDispositivos()) {
                    dispositivo.setIdpool(pool.getId());
                    dispositivo.setEndServidor("");
                    dispositivos.add(dispositivo);
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                        CustomAdapter arrayAdapter = new CustomAdapter(activity, dispositivos);
                        simpleList.setAdapter(arrayAdapter);

                }
            });



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