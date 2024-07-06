package com.projetos.marcelo.iotcontrole;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;


public class MainActivity extends AppCompatActivity implements IMqttMessageListener {

    private final Object obj = new Object();
    public CustomAdapter arrayAdapter;
    SQLiteDatabase mydatabase;
    AppCompatActivity activity;
    ListView simpleList;
    List<Dispositivo> dispositivos = new ArrayList<>();
    ClienteMQTT clienteMQTT;
    boolean inserido = false;
    boolean inicializado = false;
    String idGerado = "";

    String user = "";

    String uuidCliente = "";

    private String uuids[];

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent it = getIntent();

        //Recuperei a string da outra activity
        uuids = it.getStringArrayExtra  ("uuids");
        idGerado = it.getStringExtra("idGerado");
        user = it.getStringExtra("user");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img  = findViewById(R.id.imageView);
        this.setTitle("Controle");
        activity = this;
        //calendar.setTime(new Date());
        FloatingActionButton btCfg = findViewById(R.id.bCfg);
        btCfg.setOnClickListener(view -> {
        });
        inicializarMqtt();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void inicializarMqtt() {
        try {
            if (!inicializado) {
                inicializado = true;
                StrictMode.ThreadPolicy gfgPolicy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(gfgPolicy);
                clienteMQTT = new ClienteMQTT("ssl://f897f821.ala.us-east-1.emqxsl.com:8883", "neuverse", "M@r040370");
                clienteMQTT.mqttOptions.setSSLHostnameVerifier(null);
                clienteMQTT.iniciar(user);
                clienteMQTT.subscribe(0, this, "br/com/neuverse/servidores/events");
                simpleList = (ListView) findViewById(R.id.simpleListView);
                Login login = new Login();
                login.setUuid(idGerado);
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                String jSon = gson.toJson(login);
                for ( String uuid : uuids){
                    clienteMQTT.subscribe(0,this,"br/com/neuverse/clientes/"+idGerado+"/#");
                    new Thread() {
                        @Override
                        public void run() {

                            clienteMQTT.publicar("br/com/neuverse/servidores/"+uuid+"/info",jSon.getBytes(), 1);
                        }
                    }.start();
                }
                arrayAdapter = new CustomAdapter(activity, dispositivos, clienteMQTT,idGerado);
                simpleList.setAdapter(arrayAdapter);
                monitora();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println(new String(message.getPayload()));
        if (topic.equals("br/com/neuverse/clientes/"+idGerado+"/infoServidor")) {
            acrescentarServidorIOT(new String(message.getPayload()));
        } else if (topic.equals("br/com/neuverse/servidores/events")) {
            handleEvent(new String(message.getPayload()));
        } else if (topic.equals("br/com/neuverse/servidores/" + idGerado + "/alive")) {
            try {
                if(img.getVisibility() == View.VISIBLE){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            img.setVisibility(View.INVISIBLE);
                        }
                    });

                }
                else
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            img.setVisibility(View.VISIBLE);
                        }
                    });

            } catch (Exception e) {
                System.out.println();
            }
        }
    }

    public Drawable putImagem(Dispositivo dispositivo){
        Drawable img = null;
        switch (dispositivo.getGenero().getValor()){
            case  1:
                img = (dispositivo.getStatus().equals(Status.ON) && dispositivo.getNivelAcionamento().equals(Status.HIGH)) ||
                        (dispositivo.getStatus().equals(Status.OFF) && dispositivo.getNivelAcionamento().equals(Status.LOW))
                        ? activity.getResources().getDrawable(R.drawable.lacessa)
                        :  activity.getResources().getDrawable(R.drawable.b983w);
                break;
            case 15:
                img = activity.getResources().getDrawable(R.drawable.pushbutton);
                break;
            case 11:
            default:
                img = (dispositivo.getStatus().equals(Status.ON)&& dispositivo.getNivelAcionamento().equals(Status.HIGH)) ||
                        (dispositivo.getStatus().equals(Status.OFF) && dispositivo.getNivelAcionamento().equals(Status.LOW))
                        ? activity.getResources().getDrawable(R.drawable.intligado)
                        :  activity.getResources().getDrawable(R.drawable.intdesligado);
                break;
        }
        return img;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public synchronized void handleEvent(String json) {
        List<Pool> pools = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create()
                .fromJson(json, new TypeToken<ArrayList<Pool>>() {}.getType());

        for (Pool pool : pools) {
            for (Dispositivo dispositivo : pool.getDispositivos()) {
                Button btn = arrayAdapter.getItemByIdPoolIdDisp(pool.getId(), dispositivo.getId());
                Drawable img = putImagem(dispositivo);
                img.setBounds(0, 0, 60, 60);
                btn.setCompoundDrawables(img, null, null, null);
            }
        }
    }

    public synchronized void acrescentarServidorIOT(String jSon) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                    Type listType = new TypeToken<ArrayList<Pool>>() {
                    }.getType();
                    List<Pool> pools = gson.fromJson(jSon, listType);
                    for (Pool pool : pools) {
                        for (Dispositivo dispositivo : pool.getDispositivos()) {
                            dispositivo.setIdpool(pool.getId());
                            if (pool.getNick() != null && pool.getNick().trim().length() > 0)
                                dispositivo.setNickServidor(pool.getNick());
                            else
                                dispositivo.setNickServidor("Sem nome");
                            boolean naLista = false;
                            for (Dispositivo dsp : dispositivos) {
                                if (dsp.getIdpool().equals(pool.getId()) && dsp.getId()
                                        .equals(dispositivo.getId())) {
                                    naLista = true;
                                    break;
                                }
                            }
                            if (!naLista)
                                dispositivos.add(dispositivo);
                        }
                    }
                    activity.runOnUiThread(() -> arrayAdapter.notifyDataSetChanged());
                }
            });

        } catch (Exception e) {
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

    public void salvarUsuario(){
        /*Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        String json = gson.toJson(usuario);
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput("usuario", Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
        }*/
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


    public void monitora() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {

                        Thread.sleep(2000);
                        if (clienteMQTT.getClient().isConnected()) {

                            clienteMQTT.getClient().publish("br/com/neuverse/servidores/" + idGerado + "/alive",
                                    "alive".getBytes(),
                                    0, false);

                        } else {
                            try {
                                clienteMQTT.getClient().close();
                                clienteMQTT.setClient(null);
                                MqttClient cli = new MqttClient("ssl://f897f821.ala.us-east-1.emqxsl.com:8883", idGerado,
                                        new MqttDefaultFilePersistence(Objects.requireNonNull(System.getProperty("java.io.tmpdir"))));
                                clienteMQTT.setClient(cli);
                                clienteMQTT.getClient().setCallback(clienteMQTT);
                                clienteMQTT.mqttOptions.setSSLHostnameVerifier(null);
                                clienteMQTT.getClient().connect(clienteMQTT.getMqttOptions());


                            } catch (MqttException ex) {

                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }.start();
    }

}